package com.smart.q.smartq.repository;

import com.smart.q.smartq.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, String> {
	List<ParkingSlot> findByLocationId(String locationId);

	@Query("""
			SELECT ps
			FROM ParkingSlot ps
			WHERE ps.isActive = true
			AND ps.isOccupied = false
			AND ps.id NOT IN (
			    SELECT r.slotId
			    FROM Reservation r
			    WHERE r.status <> 'CANCELLED'
			    AND (
			        (r.startTime < :endDateTime AND r.endTime > :startDateTime)
			    )
			)
			""")
	List<ParkingSlot> findAvailableSlots(@Param("startDateTime") LocalDateTime startDateTime,
			@Param("endDateTime") LocalDateTime endDateTime);
}