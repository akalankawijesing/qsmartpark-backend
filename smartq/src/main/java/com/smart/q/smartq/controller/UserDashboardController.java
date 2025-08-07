package com.smart.q.smartq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.smart.q.smartq.security.JwtUserPrincipal;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserDashboardController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getUserDashboard(Authentication auth) {
        JwtUserPrincipal principal = (JwtUserPrincipal) auth.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User dashboard data");
        response.put("userId", principal.getUserId());
        response.put("personalStats", Map.of(
            "completedTasks", 45,
            "pendingTasks", 12,
            "totalPoints", 1250
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication auth) {
        JwtUserPrincipal principal = (JwtUserPrincipal) auth.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User profile data");
        response.put("userId", principal.getUserId());
        return ResponseEntity.ok(response);
    }
}