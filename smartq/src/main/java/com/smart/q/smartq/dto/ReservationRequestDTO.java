package com.smart.q.smartq.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Slot ID is required")
    private String slotId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Vehicle No. is required")
    private String vehicleNo;
    
    @NotNull(message = "Vehicle type is required")
    private String vehicleType;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotBlank(message = "Status is required")
    private String status;

    private String qrCode;
}
