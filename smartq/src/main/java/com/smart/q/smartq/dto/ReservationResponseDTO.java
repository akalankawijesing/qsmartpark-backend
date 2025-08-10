package com.smart.q.smartq.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {
    private String id;
    private String userId;
    private String slotId;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String qrCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
