package com.smart.q.smartq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.q.smartq.model.Cost;

import java.time.LocalDate;
import java.util.Optional;

public interface CostRepository extends JpaRepository<Cost, String> {

    @Query("""
        SELECT c FROM Cost c
        WHERE c.slotType = :slotType
          AND c.effectiveFrom <= :bookingDate
          AND (c.effectiveTo IS NULL OR c.effectiveTo >= :bookingDate)
        """)
    Optional<Cost> findActiveCostBySlotTypeAndDate(@Param("slotType") String slotType, @Param("bookingDate") LocalDate bookingDate);

}
