package com.smart.q.smartq.controller;

import com.smart.q.smartq.dto.PaymentRequestDTO;
import com.smart.q.smartq.dto.PaymentConfigResponseDTO;
import com.smart.q.smartq.dto.PaymentVerificationResponseDTO;
import com.smart.q.smartq.service.PaymentService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "https://your-frontend-domain.com"}) // Add your production domain
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Initialize payment and return configuration for PayHere JavaScript SDK
     * This replaces HTML form generation approach
     */
    @PostMapping("/initiate")
    public ResponseEntity<PaymentConfigResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {
        
        try {
            log.info("Payment initiation request received for orderId: {}", paymentRequest.getOrderId());
            
            PaymentConfigResponseDTO response = paymentService.initiatePayment(paymentRequest);
            
            log.info("Payment configuration generated successfully for orderId: {}", paymentRequest.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error initiating payment for orderId: {}", paymentRequest.getOrderId(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * PayHere webhook endpoint - receives payment notifications
     * This endpoint MUST be publicly accessible
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handlePaymentWebhook(@RequestParam Map<String, String> payload) {
        
        String orderId = payload.get("order_id");
        String statusCode = payload.get("status_code");
        
        log.info("PayHere webhook received - OrderId: {}, StatusCode: {}", orderId, statusCode);
        log.debug("Full webhook payload: {}", payload);

        try {
            paymentService.processPaymentNotification(payload);
            
            log.info("Webhook processed successfully for orderId: {}", orderId);
            return ResponseEntity.ok("OK");
            
        } catch (SecurityException se) {
            log.warn("Webhook security verification failed for orderId: {} - {}", orderId, se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
            
        } catch (Exception e) {
            log.error("Error processing webhook for orderId: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing error");
        }
    }
    
    /**
     * Verify payment status after client-side completion
     * CRITICAL: This should be called after payhere.onCompleted callback
     */
    @PostMapping("/verify/{orderId}")
    public ResponseEntity<PaymentVerificationResponseDTO> verifyPayment(@PathVariable String orderId) {
        
        try {
            log.info("Payment verification request for orderId: {}", orderId);
            
            PaymentVerificationResponseDTO response = paymentService.verifyPayment(orderId);
            
            log.info("Payment verification completed for orderId: {}, status: {}, verified: {}", 
                    orderId, response.getStatus(), response.getIsVerified());
                    
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error verifying payment for orderId: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentVerificationResponseDTO(orderId, "ERROR", false, "Verification failed"));
        }
    }
    
    /**
     * Get current payment status (for polling or status checks)
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<PaymentVerificationResponseDTO> getPaymentStatus(@PathVariable String orderId) {
        
        try {
            log.debug("Payment status request for orderId: {}", orderId);
            
            PaymentVerificationResponseDTO response = paymentService.getPaymentStatus(orderId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting payment status for orderId: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PaymentVerificationResponseDTO(orderId, "NOT_FOUND", false, "Order not found"));
        }
    }
    
    /**
     * Health check endpoint for webhook URL validation
     */
    @GetMapping("/webhook/health")
    public ResponseEntity<String> webhookHealthCheck() {
        return ResponseEntity.ok("Webhook endpoint is healthy");
    }
}