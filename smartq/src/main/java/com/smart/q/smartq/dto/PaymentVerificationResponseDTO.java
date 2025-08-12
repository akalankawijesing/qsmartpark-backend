package com.smart.q.smartq.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationResponseDTO {
    private String orderId;
    private String status;
    private String bookingStatus;
    private String paymentId;
    private Double amount;
    private String paymentMethod;
    private Boolean isVerified;
    private String statusCode;
    
    // Constructor for error cases
    public PaymentVerificationResponseDTO(String orderId, String status, Boolean isVerified, String message) {
        this.orderId = orderId;
        this.status = status;
        this.isVerified = isVerified;
        this.bookingStatus = message;
    }
}