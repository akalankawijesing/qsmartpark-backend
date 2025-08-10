package com.smart.q.smartq.service;

import com.smart.q.smartq.dto.ParkingSlotResponseDTO;
import com.smart.q.smartq.mapper.ParkingSlotMapper;
import com.smart.q.smartq.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSlotMapper parkingSlotMapper; // Manual or MapStruct

    public List<ParkingSlotResponseDTO> getAvailableSlots(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return parkingSlotRepository.findAvailableSlots(startDateTime, endDateTime)
                .stream()
                .map(parkingSlotMapper::toDTO)
                .toList();
    }
}
