package com.mealplan.dto;

import com.mealplan.entity.UserRole;
import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private UUID userId;
    private String email;
    private String username;
    private UserRole role;
    private String tokenType = "Bearer";
    private long expiresIn;
}