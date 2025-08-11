package com.smart.q.smartq.service;

import com.smart.q.smartq.dto.PaymentRequestDTO;
import com.smart.q.smartq.dto.PaymentResponseDTO;
import com.smart.q.smartq.exception.BusinessException;
import com.smart.q.smartq.model.Reservation;
import com.smart.q.smartq.repository.ReservationRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final ReservationRepository reservationRepository;

    private final String merchantSecret = System.getenv("PAYHERE_MERCHANT_SECRET");
    private final String merchantId = System.getenv("PAYHERE_MERCHANT_SECRET");
    

    @Transactional
    public void processPaymentNotification(Map<String, String> payload) {
        // Extract required fields (adjust keys based on PayHere webhook docs)
        String merchantId = payload.get("merchant_id");
        String orderId = payload.get("order_id");
        String status = payload.get("status");
        String amount = payload.get("payhere_amount");
        String currency = payload.get("payhere_currency");
        String receivedHash = payload.get("md5sig"); // Or the hash key sent by PayHere

        // Verify hash signature
        if (!verifyHash(payload, receivedHash)) {
            throw new SecurityException("Invalid hash signature");
        }

        // Validate order exists
        Optional<Reservation> reservationOpt = reservationRepository.findByOrderId(orderId);
        if (reservationOpt.isEmpty()) {
            throw new BusinessException("Reservation not found for orderId: " + orderId);
        }

        Reservation reservation = reservationOpt.get();

        // Validate amount and currency
        if (!reservation.getCost().toPlainString().equals(amount)) {
            throw new BusinessException("Payment amount mismatch");
        }
        if (!reservation.getCurrency().equalsIgnoreCase(currency)) {
            throw new BusinessException("Payment currency mismatch");
        }

        // Update reservation status based on payment status
        switch (status.toUpperCase()) {
            case "COMPLETED":
                reservation.setStatus("CONFIRMED");
                break;
            case "FAILED":
                reservation.setStatus("FAILED");
                break;
            case "PENDING":
                reservation.setStatus("PAYMENT_PENDING");
                break;
            default:
                throw new BusinessException("Unknown payment status: " + status);
        }

        reservationRepository.save(reservation);

        log.info("Updated reservation {} status to {}", reservation.getId(), reservation.getStatus());

        // Optionally: trigger notification, audit logging, etc.
    }

    private boolean verifyHash(Map<String, String> payload, String receivedHash) {
        try {
            // Construct data string for hash calculation according to PayHere webhook specs
            // For example: merchant_id + order_id + amount + currency + status + merchant_secret
            String data = payload.get("merchant_id")
                    + payload.get("order_id")
                    + payload.get("payhere_amount")
                    + payload.get("payhere_currency")
                    + payload.get("status")
                    + merchantSecret;

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            String calculatedHash = sb.toString();

            return calculatedHash.equalsIgnoreCase(receivedHash);
        } catch (Exception e) {
            log.error("Error verifying hash", e);
            return false;
        }
    }

    public PaymentResponseDTO initiatePayment(@Valid PaymentRequestDTO paymentRequest) {
        //String orderId = payload.get("order_id");
       // String status = payload.get("status");
        //String amount = payload.get("payhere_amount");
       // String currency = payload.get("payhere_currency");
       // String receivedHash = payload.get("md5sig"); // Or the hash key sent by PayHere
        String paymentUrl = "https://www.payhere.lk/pay/" + paymentRequest.getOrderId();

        // You could add signature generation or other security measures here

        return new PaymentResponseDTO(paymentUrl, "Payment initiation successful");
    }
}
