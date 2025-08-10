package com.smart.q.smartq.mapper;

import com.smart.q.smartq.dto.ParkingSlotResponseDTO;
import com.smart.q.smartq.model.ParkingSlot;
import org.springframework.stereotype.Component;

@Component
public class ParkingSlotMapperImpl implements ParkingSlotMapper {

    @Override
    public ParkingSlotResponseDTO toDTO(ParkingSlot slot) {
        if (slot == null) {
            return null;
        }

        ParkingSlotResponseDTO dto = new ParkingSlotResponseDTO();
        dto.setId(slot.getId());
        dto.setLocationId(slot.getLocationId());
        dto.setSlotCode(slot.getSlotCode());
        dto.setLevel(slot.getLevel());
        dto.setSlotType(slot.getSlotType());
        dto.setIsOccupied(slot.isOccupied());
        dto.setIsActive(slot.isActive());
        dto.setCreatedAt(slot.getCreatedAt());
        dto.setUpdatedAt(slot.getUpdatedAt());

        return dto;
    }
}
