package com.smart.q.smartq.controller;

import org.springframework.http.ResponseEntity;
import com.smart.q.smartq.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.smart.q.smartq.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public SystemController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<List<User>> getSystemUsers() {
		List<User> users = userRepository.findAll(); // Or any logic
		return ResponseEntity.ok(users);
	}

	@PostMapping("/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
		String rawPassword = (String) userData.get("passwordHash");
		String hashedPassword = passwordEncoder.encode(rawPassword);
		User user = User.builder()
				.firstName((String) userData.get("firstName"))
				.lastName((String) userData.get("lastName"))
				.email((String) userData.get("email"))
				.phone((String) userData.get("phone"))
				.isActive(userData.get("isActive") != null ? Boolean.valueOf(userData.get("isActive").toString()) : true)
				.role(User.Role.fromString((String) userData.get("role")))
				.passwordHash(hashedPassword)
				.build();
		userRepository.save(user);

		Map<String, Object> response = new HashMap<>();
		response.put("message", "User created successfully");
		response.put("data", user);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
	public ResponseEntity<Map<String, Object>> getSystemDashboard() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "System dashboard data");
		response.put("stats", Map.of("totalUsers", 150, "activeUsers", 120, "pendingRequests", 25));
		return ResponseEntity.ok(response);
	}
}