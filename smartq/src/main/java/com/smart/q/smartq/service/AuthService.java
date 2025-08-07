package com.smart.q.smartq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.q.smartq.dto.LoginRequest;
import com.smart.q.smartq.dto.RegisterRequest;
import com.smart.q.smartq.dto.UserInfoResponse;
import com.smart.q.smartq.model.User;
import com.smart.q.smartq.repository.UserRepository;
import com.smart.q.smartq.security.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.fromString(request.getRole()));
        user.setIsActive(true);

        return userRepository.save(user);
    }

    public ResponseEntity<String> login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Include roles in JWT
        Set<String> roles = Set.of(user.getRole().toString());
        String jwt = jwtUtil.generateToken(user.getUserId(), roles);

        // Build cookie with SameSite attribute using Spring's ResponseCookie
        ResponseCookie cookie = ResponseCookie.from("token", jwt)
            .httpOnly(true)
            .secure(false) // Set to true in production with HTTPS
            .path("/")
            .maxAge(60 * 60 * 24) // 1 day expire
            .sameSite("Lax") // SameSite attribute
            .build();

        // Set cookie header manually
        response.setHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok("Login successful");
    }

    public UserInfoResponse getCurrentUserInfo(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfoResponse userInfo = new UserInfoResponse();
        userInfo.setUserId(user.getUserId());
        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setRoles(Set.of(user.getRole().toString()));

        return userInfo;
    }

    public void logout(HttpServletResponse response) {
        // Clear the JWT cookie
        ResponseCookie cookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(false) // Set to true in production with HTTPS
            .path("/")
            .maxAge(0) // Expire immediately
            .sameSite("Lax")
            .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
}