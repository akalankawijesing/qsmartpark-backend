package com.smart.q.smartq.controller;

import com.smart.q.smartq.dto.ParkingSlotResponseDTO;
import com.smart.q.smartq.model.ParkingSlot;
import com.smart.q.smartq.repository.ParkingSlotRepository;
import com.smart.q.smartq.service.ParkingSlotService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parking-slots")
public class ParkingSlotController {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSlotService parkingSlotService;

    public ParkingSlotController(ParkingSlotRepository parkingSlotRepository, ParkingSlotService parkingSlotService) {
        this.parkingSlotRepository = parkingSlotRepository;
		this.parkingSlotService = parkingSlotService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> createSlot(@RequestBody ParkingSlot slot) {
        try {
            ParkingSlot savedSlot = parkingSlotRepository.save(slot);
            return ResponseEntity.ok(savedSlot);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating slot: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public List<ParkingSlot> getAllSlots() {
        return parkingSlotRepository.findAll();
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public List<ParkingSlot> getSlotsByLocation(@PathVariable String locationId) {
        return parkingSlotRepository.findByLocationId(locationId);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ParkingSlotResponseDTO>> getAvailableSlots(
            @RequestParam String startDateTime,
            @RequestParam String endDateTime
    ) {
        LocalDateTime start = LocalDateTime.parse(startDateTime.trim());
        LocalDateTime end = LocalDateTime.parse(endDateTime.trim());

        List<ParkingSlotResponseDTO> slots = parkingSlotService.getAvailableSlots(start, end);
        return ResponseEntity.ok(slots);
    }
}
