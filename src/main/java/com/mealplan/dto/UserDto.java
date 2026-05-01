package com.mealplan.dto;

import com.mealplan.entity.UserRole;
import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String username;
    private UserRole role;
}
