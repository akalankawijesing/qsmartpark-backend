package com.smart.q.smartq.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @NonNull
    private String orderId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

}
