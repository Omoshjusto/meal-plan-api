package com.mealplan.controller;

import com.mealplan.dto.LoginRequest;
import com.mealplan.dto.LoginResponse;
import com.mealplan.dto.RegisterRequest;
import com.mealplan.dto.UserDto;
import com.mealplan.service.AuthService;
import com.mealplan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * Register a new user account
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        UserDto user = userService.registerUser(request);
        log.info("User registered successfully with email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Login user and get JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        log.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user info
     */
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        return ResponseEntity.ok("Authenticated user endpoint");
    }
}