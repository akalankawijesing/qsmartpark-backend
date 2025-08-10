package com.smart.q.smartq.mapper;

import com.smart.q.smartq.dto.ReservationRequestDTO;
import com.smart.q.smartq.dto.ReservationResponseDTO;
import com.smart.q.smartq.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation toEntity(ReservationRequestDTO dto);

    ReservationResponseDTO toDTO(Reservation entity);

    void updateEntityFromDTO(ReservationRequestDTO dto, @MappingTarget Reservation entity);
}
