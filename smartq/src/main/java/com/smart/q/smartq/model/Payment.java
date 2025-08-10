package com.smart.q.smartq.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

	@Id
    @Column(name = "id", updatable = false, nullable = false)
	private String id;

	@PrePersist
	public void prePersist() {
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
	}

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;  // Unique order ID linking payment to reservation

    @Column(name = "reservation_id", nullable = false)
    private String reservationId;  // FK to reservation

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;  // PayHere merchant ID

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;  // ISO currency code e.g., LKR

    @Column(name = "payment_id", unique = true)
    private String paymentId;  // PayHere payment transaction ID

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;  // Enum: PENDING, SUCCESS, FAILED

    @Column(name = "status_message")
    private String statusMessage;  // Optional descriptive status message

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Payment() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enum for Payment Status
    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }
}
