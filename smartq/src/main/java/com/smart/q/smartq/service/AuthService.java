package com.smart.q.smartq.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.q.smartq.dto.LoginRequest;
import com.smart.q.smartq.dto.RegisterRequest;
import com.smart.q.smartq.model.User;
import com.smart.q.smartq.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        //user.setRole(request.getRole());
        user.setRole(User.Role.fromString(request.getRole()));
        user.setIsActive(true);

        return userRepository.save(user);
	}

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Ideally return a JWT token, for now just return a dummy string
        return "logged-in-token-for-user-" + user.getUserId();
    }
}
