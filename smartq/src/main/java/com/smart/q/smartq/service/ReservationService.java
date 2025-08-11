package com.smart.q.smartq.service;

import com.smart.q.smartq.dto.ReservationRequestDTO;
import com.smart.q.smartq.dto.ReservationResponseDTO;
import com.smart.q.smartq.exception.BusinessException;
import com.smart.q.smartq.exception.ResourceNotFoundException;
import com.smart.q.smartq.mapper.ReservationMapper;
import com.smart.q.smartq.model.Cost;
import com.smart.q.smartq.model.ParkingSlot;
import com.smart.q.smartq.model.Reservation;
import com.smart.q.smartq.repository.CostRepository;
import com.smart.q.smartq.repository.ParkingSlotRepository;
import com.smart.q.smartq.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final ReservationMapper reservationMapper;
	private final ParkingSlotRepository parkingSlotRepository;
	private final CostRepository costRepository;

	// Inject other services as needed
	// private final SlotService slotService;
	// private final UserService userService;
	// private final QrCodeService qrCodeService;
	// private final NotificationService notificationService;

	@Transactional
	public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO) {
	    validateReservationRequest(requestDTO);
	    validateUser(requestDTO.getUserId());
	    validateSlotAvailability(requestDTO.getSlotId(), requestDTO.getStartTime(), requestDTO.getEndTime());

	    checkUserReservationLimits(requestDTO.getUserId(), requestDTO.getStartTime());
	    checkAdvanceBookingLimits(requestDTO.getStartTime());

	    List<Reservation> locked = reservationRepository.lockOverlappingReservations(
	        requestDTO.getSlotId(),
	        requestDTO.getStartTime(),
	        requestDTO.getEndTime(),
	        List.of("CONFIRMED", "PAYMENT_PENDING", "PAYMENT_INITIATED")
	    );
	    if (!locked.isEmpty()) {
	        throw new BusinessException("Slot already booked");
	    }

	    Reservation reservation = reservationMapper.toEntity(requestDTO);
	    reservation.setCost(calculateReservationCost(requestDTO));
	    reservation.setCurrency("LKR");
	    preSaveProcessing(reservation, requestDTO);

	    Reservation saved = reservationRepository.save(reservation);
	    log.info("Reservation created: {}", saved.getId());

	    postSaveProcessing(saved);

	    return reservationMapper.toDTO(saved);
	}


	// ========== VALIDATION METHODS ==========

	private void validateReservationRequest(ReservationRequestDTO requestDTO) {
		log.debug("Validating reservation request");

		// Time validation
		if (requestDTO.getStartTime().isAfter(requestDTO.getEndTime())) {
			throw new BusinessException("Start time cannot be after end time");
		}

		if (requestDTO.getStartTime().isBefore(LocalDateTime.now())) {
			throw new BusinessException("Cannot create reservation in the past");
		}

		// Duration validation (example: minimum 30 minutes, maximum 4 hours)
		long durationMinutes = java.time.Duration.between(requestDTO.getStartTime(), requestDTO.getEndTime())
				.toMinutes();

		if (durationMinutes < 30) {
			throw new BusinessException("Reservation must be at least 30 minutes long");
		}

		if (durationMinutes > 240) {
			throw new BusinessException("Reservation cannot exceed 4 hours");
		}

		// Status validation
		if (!isValidStatus(requestDTO.getStatus())) {
			throw new BusinessException("Invalid reservation status: " + requestDTO.getStatus());
		}
	}

	private void checkBusinessRules(ReservationRequestDTO requestDTO) {
		log.debug("Checking business rules for reservation");

		// 1. Check if user exists and is active
		validateUser(requestDTO.getUserId());

		// 2. Check if slot exists and is available
		validateSlotAvailability(requestDTO.getSlotId(), requestDTO.getStartTime(), requestDTO.getEndTime());

		// 3. Check for overlapping reservations
		//checkForOverlappingReservations(requestDTO);
		createReservationWithLock(requestDTO);

		// 4. Check user's reservation limits
		checkUserReservationLimits(requestDTO.getUserId(), requestDTO.getStartTime());

		// 5. Check advance booking limits
		checkAdvanceBookingLimits(requestDTO.getStartTime());
	}

	private void validateUser(String userId) {
		// Example: Check if user exists and is active
		// User user = userService.findById(userId);
		// if (user == null || !user.isActive()) {
		// throw new BusinessException("User not found or inactive: " + userId);
		// }

		// For now, just basic validation
		if (userId == null || userId.trim().isEmpty()) {
			throw new BusinessException("Invalid user ID");
		}
	}

	private void validateSlotAvailability(String slotId, LocalDateTime startTime, LocalDateTime endTime) {
		// Example: Check if slot exists and is available during the requested time
		// Slot slot = slotService.findById(slotId);
		// if (slot == null) {
		// throw new ResourceNotFoundException("Slot not found: " + slotId);
		// }
		//
		// if (!slotService.isAvailable(slotId, startTime, endTime)) {
		// throw new BusinessException("Slot is not available during the requested
		// time");
		// }

		// For now, just basic validation
		if (slotId == null || slotId.trim().isEmpty()) {
			throw new BusinessException("Invalid slot ID");
		}
	}

	private void checkForOverlappingReservations(ReservationRequestDTO requestDTO) {
		List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
				requestDTO.getSlotId(), requestDTO.getStartTime(), requestDTO.getEndTime(),
				List.of("CONFIRMED", "PAYMENT_PENDING","PAYMENT_INITIATED") // Only check active statuses
		);

		if (!overlappingReservations.isEmpty()) {
			throw new BusinessException("Time slot is already reserved");
		}
	}

	private void checkUserReservationLimits(String userId, LocalDateTime startTime) {
		// User can only have 3 active reservations at a time
		long activeReservations = reservationRepository.countActiveReservationsByUser(userId,
				List.of("CONFIRMED", "PAYMENT_PENDING","PAYMENT_INITIATED"));

		if (activeReservations >= 12) {
			throw new BusinessException("User has reached maximum reservation limit (12)");
		}

		// User can only book 1 slot per day
		long reservationsOnSameDay = reservationRepository.countReservationsByUserAndDate(userId,
				startTime.toLocalDate(), List.of("CONFIRMED", "PAYMENT_PENDING"));

		if (reservationsOnSameDay >= 4) {
			throw new BusinessException("User can only book one slot per day");
		}
	}

	private void checkAdvanceBookingLimits(LocalDateTime startTime) {
		// Cannot book more than 30 days in advance
		LocalDateTime maxAdvanceDate = LocalDateTime.now().plusDays(30);
		if (startTime.isAfter(maxAdvanceDate)) {
			throw new BusinessException("Cannot book more than 30 days in advance");
		}

		// Must book at least 1 hour in advance
		LocalDateTime minAdvanceTime = LocalDateTime.now().plusHours(1);
		if (startTime.isBefore(minAdvanceTime)) {
			throw new BusinessException("Must book at least 1 hour in advance");
		}
	}

	private BigDecimal calculateReservationCost(ReservationRequestDTO requestDTO) {

		ParkingSlot slot = parkingSlotRepository.findById(requestDTO.getSlotId())
				.orElseThrow(() -> new BusinessException("Invalid slot ID"));
		String slotType = slot.getSlotType();

		LocalDate bookingDate = requestDTO.getDate();
		Cost cost = costRepository.findActiveCostBySlotTypeAndDate(slotType, bookingDate).orElseThrow(
				() -> new BusinessException("No cost defined for slot type: " + slotType + " on date: " + bookingDate));

		long durationMinutes = java.time.Duration.between(requestDTO.getStartTime(), requestDTO.getEndTime())
				.toMinutes();

		BigDecimal totalCost = BigDecimal.ZERO;

		if (durationMinutes <= 30) {
			totalCost = cost.getRatePerHalfHour();
		} else {
			long hours = durationMinutes / 60;
			long remainingMinutes = durationMinutes % 60;

			totalCost = cost.getRatePerHour().multiply(BigDecimal.valueOf(hours));
			if (remainingMinutes > 0) {
				totalCost = totalCost.add(cost.getRatePerHalfHour());
			}
		}

		return totalCost;
	}
	// ========== PROCESSING METHODS ==========
	
	@Transactional
	public void createReservationWithLock(ReservationRequestDTO requestDTO) {
	    // Validate inputs first (business validations, user, slot checks)
	    validateReservationRequest(requestDTO);
	    validateUser(requestDTO.getUserId());
	    validateSlotAvailability(requestDTO.getSlotId(), requestDTO.getStartTime(), requestDTO.getEndTime());

	    // Lock overlapping reservations inside this transaction
	    List<Reservation> lockedReservations = reservationRepository.lockOverlappingReservations(
	        requestDTO.getSlotId(),
	        requestDTO.getStartTime(),
	        requestDTO.getEndTime(),
	        List.of("CONFIRMED", "PAYMENT_PENDING","PAYMENT_INITIATED") // statuses to consider locked
	    );

	    if (!lockedReservations.isEmpty()) {
	        throw new BusinessException("Slot already booked for the selected time range");
	    }

	}

	private void preSaveProcessing(Reservation reservation, ReservationRequestDTO requestDTO) {
		log.debug("Pre-save processing for reservation");

		// 1. Generate QR Code if not provided
		if (reservation.getQrCode() == null || reservation.getQrCode().isEmpty()) {
			reservation.setQrCode(generateQrCode(reservation));
		}

		// 2. Set default status if needed
		if (reservation.getStatus() == null) {
			reservation.setStatus("PENDING");
		}

		// 3. Apply business logic transformations
		// Example: Convert status to uppercase
		reservation.setStatus(reservation.getStatus().toUpperCase());
	}

	private void postSaveProcessing(Reservation savedReservation) {
		log.debug("Post-save processing for reservation ID: {}", savedReservation.getId());

		// These operations can be async using @Async annotation
		try {
			// 1. Send confirmation notification
			// notificationService.sendReservationConfirmation(savedReservation);

			// 2. Update slot availability
			// slotService.updateAvailability(savedReservation.getSlotId());

			// 3. Log audit trail
			// auditService.logReservationCreated(savedReservation);

		} catch (Exception e) {
			log.error("Error in post-save processing for reservation {}: {}", savedReservation.getId(), e.getMessage());
			// Don't throw exception here as the main transaction is already committed
		}
	}

	// ========== HELPER METHODS ==========

	private boolean isValidStatus(String status) {
		List<String> validStatuses = List.of("PENDING","PAYMENT_PENDING","PAYMENT_INITIATED","CONFIRMED", "CANCELLED", "COMPLETED", "EXPIRED", "FAILED");
		return validStatuses.contains(status.toUpperCase());
	}

	private String generateQrCode(Reservation reservation) {
		// Simple QR code generation logic
		return "QR_" + reservation.getUserId() + "_" + reservation.getSlotId() + "_" + System.currentTimeMillis();
	}

	// ========== OTHER SERVICE METHODS ==========

	@Transactional(readOnly = true)
	public List<ReservationResponseDTO> getAllReservations() {
		log.debug("Fetching all reservations");
		return reservationRepository.findAll().stream().map(reservationMapper::toDTO).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ReservationResponseDTO getReservationById(String id) {
		log.debug("Fetching reservation by ID: {}", id);
		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
		return reservationMapper.toDTO(reservation);
	}

	public ReservationResponseDTO updateReservation(String id, ReservationRequestDTO requestDTO) {
		log.info("Updating reservation ID: {}", id);

		Reservation existingReservation = reservationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));

		// Validate update request
		validateReservationUpdate(existingReservation, requestDTO);

		// Update entity
		reservationMapper.updateEntityFromDTO(requestDTO, existingReservation);

		// Save changes
		Reservation updatedReservation = reservationRepository.save(existingReservation);

		return reservationMapper.toDTO(updatedReservation);
	}

	private void validateReservationUpdate(Reservation existing, ReservationRequestDTO requestDTO) {
		// Example: Cannot modify confirmed reservations
		if ("CONFIRMED".equals(existing.getStatus()) && !existing.getStartTime().equals(requestDTO.getStartTime())) {
			throw new BusinessException("Cannot modify time of confirmed reservation");
		}

		// Apply same validations as create
		validateReservationRequest(requestDTO);
	}
}