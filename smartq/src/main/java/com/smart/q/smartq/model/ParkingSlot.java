package com.smart.q.smartq.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "parking_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlot {
	@Id
    @Column(name = "id", updatable = false, nullable = false)
	private String id;

	@PrePersist
	public void prePersist() {
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
	}

	@Column(name = "location_id", nullable = false)
	private String locationId;

	@Column(name = "slot_code")
	private String slotCode;

	@Column(name = "slot_type", nullable = false)
	private String slotType;

	private int level;
	
	@Column(name = "is_occupied")
	private boolean isOccupied;
	
	@Column(name = "is_active")
	private boolean isActive;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
