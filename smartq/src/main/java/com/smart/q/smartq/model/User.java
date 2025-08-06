package com.smart.q.smartq.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@Column(name = "user_id", updatable = false, nullable = false)
	private String userId;

	@PrePersist
	public void generateId() {
	    this.userId = UUID.randomUUID().toString();
	}

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_active")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role")
    private Role role;

    @Column(name = "password_hash")
    private String passwordHash;

    public enum Role {
        ADMIN,
        STAFF,
        USER;

        public static Role fromString(String role) {
            try {
                return Role.valueOf(role.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }

    }

}

