package com.smart.q.smartq.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "cost_master")
public class Cost {

	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	@PrePersist
	public void prePersist() {
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
	}

	@Column(name = "slot_type", nullable = false, length = 50)
	private String slotType;

	@Column(name = "rate_per_hour", nullable = false, precision = 10, scale = 2)
	private BigDecimal ratePerHour;

	@Column(name = "rate_per_half_hour", precision = 10, scale = 2)
	private BigDecimal ratePerHalfHour;

	@Column(name = "effective_from", nullable = false)
	private LocalDate effectiveFrom;

	@Column(name = "effective_to")
	private LocalDate effectiveTo;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;

	@Column(name = "description")
	private String description;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Cost() {
		this.id = UUID.randomUUID().toString();
		this.createdAt = LocalDateTime.now();
		this.currency = "LKR";
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
