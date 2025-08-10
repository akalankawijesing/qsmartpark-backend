package com.smart.q.smartq.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParkingSlotResponseDTO {
    private String id;
    private String locationId;
    private String slotCode;
    private String slotType;
    private Integer level;
    private Boolean isOccupied;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
}
