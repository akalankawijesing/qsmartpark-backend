package com.smart.q.smartq.repository;

import com.smart.q.smartq.model.Reservation;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

	   
	// Find overlapping reservations for a specific slot
	@Query("SELECT r FROM Reservation r WHERE r.slotId = :slotId " + "AND r.status IN :statuses "
			+ "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
	List<Reservation> findOverlappingReservations(@Param("slotId") String slotId,
			@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
			@Param("statuses") List<String> statuses);

	// Count active reservations by user
	@Query("SELECT COUNT(r) FROM Reservation r WHERE r.userId = :userId " + "AND r.status IN :statuses "
			+ "AND r.endTime > :currentTime")
	long countActiveReservationsByUser(@Param("userId") String userId, @Param("statuses") List<String> statuses,
			@Param("currentTime") LocalDateTime currentTime);

	// Overload for current time
	default long countActiveReservationsByUser(String userId, List<String> statuses) {
		return countActiveReservationsByUser(userId, statuses, LocalDateTime.now());
	}

	// Count reservations by user and date
	@Query("SELECT COUNT(r) FROM Reservation r WHERE r.userId = :userId " + "AND DATE(r.startTime) = :date "
			+ "AND r.status IN :statuses")
	long countReservationsByUserAndDate(@Param("userId") String userId, @Param("date") LocalDate date,
			@Param("statuses") List<String> statuses);

	// Find reservations by user
	List<Reservation> findByUserIdAndStatusInOrderByStartTimeDesc(String userId, List<String> statuses);

	// Find reservations by slot
	List<Reservation> findBySlotIdAndStatusInOrderByStartTimeAsc(String slotId, List<String> statuses);

	// Find reservations by time range
	@Query("SELECT r FROM Reservation r WHERE r.startTime >= :startTime " + "AND r.endTime <= :endTime "
			+ "AND r.status IN :statuses " + "ORDER BY r.startTime ASC")
	List<Reservation> findReservationsInTimeRange(@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime, @Param("statuses") List<String> statuses);

	// Find upcoming reservations for notifications
	@Query("SELECT r FROM Reservation r WHERE r.startTime BETWEEN :now AND :futureTime " + "AND r.status = 'CONFIRMED'")
	List<Reservation> findUpcomingReservations(@Param("now") LocalDateTime now,
			@Param("futureTime") LocalDateTime futureTime);

	// Find expired reservations that need status update
	@Query("SELECT r FROM Reservation r WHERE r.endTime < :currentTime " + "AND r.status IN ('CONFIRMED', 'PENDING')")
	List<Reservation> findExpiredReservations(@Param("currentTime") LocalDateTime currentTime);

	// Lock overlapping slot reservations,avoids race conditions when updating
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM Reservation r WHERE r.slotId = :slotId " +
	       "AND r.status IN :statuses " +
	       "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
	List<Reservation> lockOverlappingReservations(
	    @Param("slotId") String slotId,
	    @Param("startTime") LocalDateTime startTime,
	    @Param("endTime") LocalDateTime endTime,
	    @Param("statuses") List<String> statuses);

	Optional<Reservation> findByOrderId(String orderId);
}