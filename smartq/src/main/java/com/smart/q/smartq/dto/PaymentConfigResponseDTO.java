package com.smart.q.smartq.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfigResponseDTO {
    private Map<String, Object> paymentConfig;
    private String message;
}