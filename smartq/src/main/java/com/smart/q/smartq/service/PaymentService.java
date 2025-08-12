package com.smart.q.smartq.service;

import com.smart.q.smartq.config.PayHereConfig;
import com.smart.q.smartq.dto.PaymentRequestDTO;
import com.smart.q.smartq.dto.PaymentConfigResponseDTO;
import com.smart.q.smartq.dto.PaymentVerificationResponseDTO;
import com.smart.q.smartq.exception.BusinessException;
import com.smart.q.smartq.model.Reservation;
import com.smart.q.smartq.repository.ReservationRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final ReservationRepository reservationRepository;
    private final PayHereConfig payHereConfig;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    /**
     * Initiate payment and return configuration for PayHere JavaScript SDK
     * This replaces HTML form generation approach
     */
    @Transactional
    public PaymentConfigResponseDTO initiatePayment(@Valid PaymentRequestDTO paymentRequest) {
        
        String orderId = paymentRequest.getOrderId();
        
        // Find reservation by orderId (not by ID)
        Reservation reservation = reservationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException("Reservation not found for orderId: " + orderId));
        
        // Validate reservation is in correct state
        if (!"PAYMENT_PENDING".equals(reservation.getStatus())) {
            throw new BusinessException("Reservation is not in PAYMENT_PENDING state. Current status: " + reservation.getStatus());
        }
        
        // Update status to PAYMENT_INITIATED
        reservation.setStatus("PAYMENT_INITIATED");
        reservation.setPaymentInitiatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        // Generate PayHere configuration for JavaScript SDK
        Map<String, Object> paymentConfig = generatePayHereConfig(reservation, paymentRequest);
        
        // Generate security hash
        String hash = generatePayHereHash(paymentConfig);
        paymentConfig.put("hash", hash);
        
        log.info("Payment initiated for order: {} with amount: {}", orderId, reservation.getCost());
        
        return new PaymentConfigResponseDTO(paymentConfig, "Payment configuration generated successfully");
    }
    
    /**
     * Generate payment configuration for PayHere JavaScript SDK
     */
    private Map<String, Object> generatePayHereConfig(Reservation reservation, PaymentRequestDTO paymentRequest) {
        Map<String, Object> config = new HashMap<>();
        
        // PayHere required parameters
        config.put("sandbox", payHereConfig.isSandbox());
        config.put("merchantId", payHereConfig.getMerchantId());
        config.put("returnUrl", frontendUrl + "/payment/success");
        config.put("cancelUrl", frontendUrl + "/payment/cancel");
        config.put("notifyUrl", backendUrl + "/api/payments/webhook");
        config.put("orderId", reservation.getOrderId());
        config.put("items", "Booking Slot On " + reservation.getStartTime());
        config.put("currency", reservation.getCurrency());
        config.put("amount", reservation.getCost().doubleValue());
        
        // Customer details
        config.put("firstName", paymentRequest.getFirstName());
        config.put("lastName", paymentRequest.getLastName());
        config.put("email", paymentRequest.getEmail());
        config.put("phone", paymentRequest.getPhone());
        config.put("address", paymentRequest.getAddress() != null ? paymentRequest.getAddress() : "");
        config.put("city", paymentRequest.getCity() != null ? paymentRequest.getCity() : "Colombo");
        config.put("country", "Sri Lanka");
        
        return config;
    }
    
    /**
     * Generate PayHere security hash for payment initiation
     * Formula: MD5(merchant_id + order_id + amount + currency + MD5(merchant_secret).toUpperCase()).toUpperCase()
     */
    private String generatePayHereHash(Map<String, Object> config) {
        String merchantSecret = payHereConfig.getMerchantSecret();
        
        String hashString = config.get("merchantId").toString() + 
                           config.get("orderId").toString() + 
                           String.format("%.2f", (Double) config.get("amount")) + 
                           config.get("currency").toString() + 
                           DigestUtils.md5Hex(merchantSecret).toUpperCase();
        
        String calculatedHash = DigestUtils.md5Hex(hashString).toUpperCase();
        
        log.debug("Hash calculation for order {}: hashString={}, hash={}", 
                 config.get("orderId"), hashString, calculatedHash);
        
        return calculatedHash;
    }
    
    /**
     * Process PayHere webhook notification with enhanced security
     */
    @Transactional
    public void processPaymentNotification(Map<String, String> payload) {
        
        // Extract webhook parameters
        String merchantId = payload.get("merchant_id");
        String orderId = payload.get("order_id");
        String paymentId = payload.get("payment_id");
        String payhereAmount = payload.get("payhere_amount");
        String payhereCurrency = payload.get("payhere_currency");
        String statusCode = payload.get("status_code");
        String md5sig = payload.get("md5sig");
        String method = payload.get("method");
        
        log.info("Processing webhook for order: {}, status: {}, paymentId: {}", orderId, statusCode, paymentId);
        
        // Verify webhook signature (CRITICAL SECURITY CHECK)
        if (!verifyWebhookSignature(merchantId, orderId, payhereAmount, payhereCurrency, statusCode, md5sig)) {
            log.error("Invalid webhook signature for order: {}. Received hash: {}", orderId, md5sig);
            throw new SecurityException("Invalid webhook signature");
        }
        
        // Find reservation
        Optional<Reservation> reservationOpt = reservationRepository.findByOrderId(orderId);
        if (reservationOpt.isEmpty()) {
            log.error("Reservation not found for orderId: {}", orderId);
            throw new BusinessException("Reservation not found for orderId: " + orderId);
        }
        
        Reservation reservation = reservationOpt.get();
        
        // Validate merchant ID
        if (!payHereConfig.getMerchantId().equals(merchantId)) {
            log.error("Merchant ID mismatch. Expected: {}, Received: {}", payHereConfig.getMerchantId(), merchantId);
            throw new BusinessException("Merchant ID mismatch");
        }
        
        // Validate amount and currency
        String expectedAmount = String.format("%.2f", reservation.getCost().doubleValue());
        if (!expectedAmount.equals(payhereAmount)) {
            log.error("Amount mismatch for order {}. Expected: {}, Received: {}", orderId, expectedAmount, payhereAmount);
            throw new BusinessException("Payment amount mismatch. Expected: " + expectedAmount + ", Received: " + payhereAmount);
        }
        
        if (!reservation.getCurrency().equalsIgnoreCase(payhereCurrency)) {
            log.error("Currency mismatch for order {}. Expected: {}, Received: {}", orderId, reservation.getCurrency(), payhereCurrency);
            throw new BusinessException("Payment currency mismatch");
        }
        
        // Check for duplicate processing (idempotency)
        if (paymentId != null && paymentId.equals(reservation.getPaymentId()) && 
            statusCode.equals(reservation.getPaymentStatusCode())) {
            log.info("Webhook already processed for order: {}, paymentId: {}", orderId, paymentId);
            return; // Already processed
        }
        
        // Update reservation based on PayHere status code
        String previousStatus = reservation.getStatus();
        updateReservationStatus(reservation, statusCode, paymentId, method, payload);
        
        // Save reservation
        reservationRepository.save(reservation);
        
        log.info("Updated reservation {} status from {} to {} (PayHere status: {})", 
                reservation.getId(), previousStatus, reservation.getStatus(), statusCode);
        
        // Log webhook for audit trail
        logWebhookEvent(orderId, statusCode, paymentId, payload);
    }
    
    /**
     * Verify PayHere webhook signature
     * Formula: MD5(merchant_id + order_id + payhere_amount + payhere_currency + status_code + MD5(merchant_secret).toUpperCase()).toUpperCase()
     */
    private boolean verifyWebhookSignature(String merchantId, String orderId, String amount, 
                                         String currency, String statusCode, String receivedHash) {
        try {
            String merchantSecret = payHereConfig.getMerchantSecret();
            
            String hashString = merchantId + orderId + amount + currency + statusCode + 
                               DigestUtils.md5Hex(merchantSecret).toUpperCase();
            
            String calculatedHash = DigestUtils.md5Hex(hashString).toUpperCase();
            
            boolean isValid = calculatedHash.equals(receivedHash);
            
            if (!isValid) {
                log.error("Webhook signature verification failed for order: {}. Expected: {}, Received: {}, HashString: {}", 
                         orderId, calculatedHash, receivedHash, hashString);
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("Error verifying webhook signature for order: " + orderId, e);
            return false;
        }
    }
    
    /**
     * Update reservation status based on PayHere status code
     */
    private void updateReservationStatus(Reservation reservation, String statusCode, 
                                       String paymentId, String method, Map<String, String> payload) {
        
        LocalDateTime now = LocalDateTime.now();
        
        switch (statusCode) {
            case "2": // Success
                reservation.setStatus("PAID");
                reservation.setBookingStatus("CONFIRMED");
                reservation.setPaymentCompletedAt(now);
                log.info("Payment successful for reservation: {}", reservation.getId());
                break;
                
            case "0": // Pending
                reservation.setStatus("PAYMENT_PENDING");
                log.info("Payment pending for reservation: {}", reservation.getId());
                break;
                
            case "-1": // Canceled
                reservation.setStatus("PAYMENT_CANCELED");
                reservation.setBookingStatus("CANCELLED");
                log.info("Payment canceled for reservation: {}", reservation.getId());
                break;
                
            case "-2": // Failed
                reservation.setStatus("PAYMENT_FAILED");
                reservation.setBookingStatus("PAYMENT_FAILED");
                log.warn("Payment failed for reservation: {}", reservation.getId());
                break;
                
            case "-3": // Charged Back
                reservation.setStatus("PAYMENT_CHARGEBACK");
                reservation.setBookingStatus("CHARGEBACK");
                log.warn("Payment charged back for reservation: {}", reservation.getId());
                break;
                
            default:
                log.warn("Unknown PayHere status code: {} for reservation: {}", statusCode, reservation.getId());
                return; // Don't update if unknown status
        }
        
        // Set payment details
        reservation.setPaymentId(paymentId);
        reservation.setPaymentMethod(method);
        reservation.setPaymentStatusCode(statusCode);
        reservation.setLastUpdated(now);
    }
    
    /**
     * Verify payment status for frontend (after client-side completion)
     */
    @Transactional(readOnly = true)
    public PaymentVerificationResponseDTO verifyPayment(String orderId) {
        
        Optional<Reservation> reservationOpt = reservationRepository.findByOrderId(orderId);
        if (reservationOpt.isEmpty()) {
            throw new BusinessException("Reservation not found for orderId: " + orderId);
        }
        
        Reservation reservation = reservationOpt.get();
        
        PaymentVerificationResponseDTO response = new PaymentVerificationResponseDTO();
        response.setOrderId(orderId);
        response.setStatus(reservation.getStatus());
        response.setBookingStatus(reservation.getBookingStatus());
        response.setPaymentId(reservation.getPaymentId());
        response.setAmount(reservation.getCost().doubleValue());
        response.setPaymentMethod(reservation.getPaymentMethod());
        response.setIsVerified(isPaymentVerified(reservation));
        response.setStatusCode(reservation.getPaymentStatusCode());
        
        return response;
    }
    
    /**
     * Check if payment is verified (received webhook and status is PAID)
     */
    private boolean isPaymentVerified(Reservation reservation) {
        return "PAID".equals(reservation.getStatus()) && 
               reservation.getPaymentId() != null &&
               "2".equals(reservation.getPaymentStatusCode());
    }
    
    /**
     * Log webhook event for audit trail
     */
    private void logWebhookEvent(String orderId, String statusCode, String paymentId, Map<String, String> payload) {
        // You can implement this to log to database or external logging service
        log.info("Webhook Event - OrderId: {}, StatusCode: {}, PaymentId: {}, Payload: {}", 
                orderId, statusCode, paymentId, payload);
        
        // Optional: Save to payment_logs table for audit
        // paymentLogService.logWebhookReceived(orderId, statusCode, paymentId, payload);
    }
    
    /**
     * Get payment status for polling
     */
    @Transactional(readOnly = true)
    public PaymentVerificationResponseDTO getPaymentStatus(String orderId) {
        return verifyPayment(orderId);
    }
}