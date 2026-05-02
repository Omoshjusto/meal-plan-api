package com.mealplan.service;

import com.mealplan.dto.RegisterRequest;
import com.mealplan.dto.UserDto;
import com.mealplan.entity.User;
import com.mealplan.entity.UserRole;
import com.mealplan.exception.DuplicateEmailException;
import com.mealplan.exception.UserNotFoundException;
import com.mealplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    public UserDto registerUser(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: Email already exists: {}", request.getEmail());
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed: Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CONSUMER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", request.getEmail());
        return toDto(savedUser);
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get user by ID
     */
    public Optional<UserDto> getUserByIdOptional(UUID id) {
        return userRepository.findById(id).map(this::toDto);
    }

    /**
     * Get user by ID (throws exception if not found)
     */
    public UserDto getUserDtoById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return toDto(user);
    }

    /**
     * Get user entity by ID (throws exception if not found)
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Validate password
     */
    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * Promote user to creator
     */
    public UserDto promoteToCreator(UUID userId) {
        User user = getUserById(userId);
        user.setRole(UserRole.CREATOR);
        User updated = userRepository.save(user);
        log.info("User promoted to CREATOR: {}", userId);
        return toDto(updated);
    }

    /**
     * Convert User entity to UserDto
     */
    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}