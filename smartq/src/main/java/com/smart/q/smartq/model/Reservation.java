package com.smart.q.smartq.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "slot_id", nullable = false)
    private String slotId;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent(message = "Date must be today or in the future")
    @Column(name = "date", nullable = false)  // Added this line
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "vehicle_no", nullable = false)
    private String vehicleNo;
    
    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "status", nullable = false)
    private String status;
    
	@Column(name = "cost", precision = 10, scale = 2)
	private BigDecimal cost;

	@Column(name = "order_id", nullable = false, unique = true)
	private String orderId;  // Unique order ID linking payment to reservation
	
	
	@Column(name = "currency", nullable = false, length = 3)
	private String currency;
	
    @Column(name = "qr_code")
    private String qrCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    
    // Payment-related fields
    @Column(name = "payment_id")
    private String paymentId;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_status_code")
    private String paymentStatusCode;
    
    @Column(name = "booking_status")
    private String bookingStatus;
    
    @Column(name = "payment_initiated_at")
    private LocalDateTime paymentInitiatedAt;
    
    @Column(name = "payment_completed_at")
    private LocalDateTime paymentCompletedAt;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if(orderId == null) {
			orderId = UUID.randomUUID().toString(); // Generate a unique order ID if not set
		}
    }
}