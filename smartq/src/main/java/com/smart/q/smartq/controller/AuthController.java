package com.smart.q.smartq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.smart.q.smartq.dto.LoginRequest;
import com.smart.q.smartq.dto.RegisterRequest;
import com.smart.q.smartq.dto.UserInfoResponse;
import com.smart.q.smartq.model.User;
import com.smart.q.smartq.security.JwtUserPrincipal;
import com.smart.q.smartq.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
		User newUser = authService.register(request);
		return ResponseEntity.ok(newUser);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
		ResponseEntity<String> result = authService.login(request, response);
		return result;
	}

	@GetMapping("/me")
	public ResponseEntity<UserInfoResponse> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(401).build();
		}

		if (authentication.getPrincipal() instanceof JwtUserPrincipal) {
			JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
			UserInfoResponse userInfo = authService.getCurrentUserInfo(principal.getUserId());
			return ResponseEntity.ok(userInfo);
		}

		return ResponseEntity.status(401).build();
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletResponse response) {
		authService.logout(response);
		return ResponseEntity.ok("Logout successful");
	}
}