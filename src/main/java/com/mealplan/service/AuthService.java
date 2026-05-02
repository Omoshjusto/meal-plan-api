package com.mealplan.service;

import com.mealplan.dto.LoginRequest;
import com.mealplan.dto.LoginResponse;
import com.mealplan.entity.User;
import com.mealplan.exception.UserNotFoundException;
import com.mealplan.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Login user and return JWT token
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());

        // Find user by email
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found with email: {}", request.getEmail());
                    return new UserNotFoundException("User not found with email: " + request.getEmail());
                });

        // Validate password
        if (!userService.validatePassword(user, request.getPassword())) {
            log.warn("Login failed: Invalid password for user: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getUsername());

        log.info("User logged in successfully: {}", request.getEmail());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .tokenType("Bearer")
                .expiresIn(86400000) // 24 hours in milliseconds
                .build();
    }
}