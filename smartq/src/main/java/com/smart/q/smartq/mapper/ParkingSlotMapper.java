package com.smart.q.smartq.mapper;

import com.smart.q.smartq.dto.ParkingSlotResponseDTO;
import com.smart.q.smartq.model.ParkingSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParkingSlotMapper {
    ParkingSlotResponseDTO toDTO(ParkingSlot slot);
}
