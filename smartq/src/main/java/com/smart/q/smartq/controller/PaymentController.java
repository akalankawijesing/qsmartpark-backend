package com.smart.q.smartq.controller;

import com.smart.q.smartq.dto.PaymentRequestDTO;
import com.smart.q.smartq.dto.PaymentResponseDTO;
import com.smart.q.smartq.service.PaymentService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {
    	
        PaymentResponseDTO response = paymentService.initiatePayment(paymentRequest);
        return ResponseEntity.ok(response);
    }
    
    
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody Map<String, String> payload) {
        log.info("Received payment webhook: {}", payload);

        try {
            paymentService.processPaymentNotification(payload);
            return ResponseEntity.ok("OK");
        } catch (SecurityException se) {
            log.warn("Payment webhook hash verification failed: {}", se.getMessage());
            return ResponseEntity.status(403).body("Invalid hash");
        } catch (Exception e) {
            log.error("Error processing payment webhook: ", e);
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
