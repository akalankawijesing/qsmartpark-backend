package com.smart.q.smartq.mapper;

import com.smart.q.smartq.dto.ReservationRequestDTO;
import com.smart.q.smartq.dto.ReservationResponseDTO;
import com.smart.q.smartq.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapperImpl implements ReservationMapper {

    @Override
    public Reservation toEntity(ReservationRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Reservation reservation = new Reservation();
        
        // Map fields from DTO to Entity
        reservation.setUserId(dto.getUserId());
        reservation.setSlotId(dto.getSlotId());
        reservation.setDate(dto.getDate());
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setStatus(dto.getStatus());
        reservation.setQrCode(dto.getQrCode());
        // Note: id, createdAt, updatedAt are handled by JPA annotations
        
        return reservation;
    }

    @Override
    public ReservationResponseDTO toDTO(Reservation entity) {
        if (entity == null) {
            return null;
        }

        ReservationResponseDTO dto = new ReservationResponseDTO();
        
        // Map all fields from Entity to DTO
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setSlotId(entity.getSlotId());
        dto.setDate(entity.getDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setStatus(entity.getStatus());
        dto.setQrCode(entity.getQrCode());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }

    @Override
    public void updateEntityFromDTO(ReservationRequestDTO dto, Reservation entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        // Update entity fields from DTO (excluding ID and audit fields)
        entity.setUserId(dto.getUserId());
        entity.setSlotId(dto.getSlotId());
        entity.setDate(dto.getDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setStatus(dto.getStatus());
        entity.setQrCode(dto.getQrCode());
        // Note: Don't update id, createdAt, updatedAt - they're managed by JPA
    }
}