package com.mealplan.controller;

import com.mealplan.entity.UserRole;
import com.mealplan.service.UserService;
import com.mealplan.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        // To be implemented with repository method to get all users
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/users/creators")
    public ResponseEntity<List<UserDto>> getCreators() {
        // To be implemented to filter creators
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/users/browsers")
    public ResponseEntity<List<UserDto>> getBrowsers() {
        // To be implemented to filter consumers
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/analytics")
    public ResponseEntity<String> getSystemAnalytics() {
        return ResponseEntity.ok("System analytics data");
    }
}
