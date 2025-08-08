package com.smart.q.smartq.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.q.smartq.model.User;
import com.smart.q.smartq.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
      this.userRepository = userRepository;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
  public List<User> getAllUsers() {
	  
      return userRepository.findAll();
  }

  @PostMapping
  public ResponseEntity<?> createUser(@RequestBody User user) {
      try {
          User savedUser = userRepository.save(user);
          return ResponseEntity.ok(savedUser);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error creating user: " + e.getMessage());
      }
  }
}