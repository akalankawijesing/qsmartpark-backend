package com.smart.q.smartq.controller;

import com.smart.q.smartq.dto.PaymentRequestDTO;
import com.smart.q.smartq.dto.PaymentResponseDTO;
import com.smart.q.smartq.service.PaymentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {

        PaymentResponseDTO response = paymentService.initiatePayment(paymentRequest);
        return ResponseEntity.ok(response);
    }
}
