package com.smart.q.smartq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getSystemUsers(Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "System users data");
        response.put("user", auth.getName());
        response.put("authorities", auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User created successfully");
        response.put("data", userData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getSystemDashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "System dashboard data");
        response.put("stats", Map.of(
            "totalUsers", 150,
            "activeUsers", 120,
            "pendingRequests", 25
        ));
        return ResponseEntity.ok(response);
    }
}