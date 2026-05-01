package com.mealplan.dto;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanDto {
    private UUID id;
    private UUID creatorId;
    private String creatorUsername;
    private String title;
    private String description;
    private String cuisine;
    private String diet;

    private String mondayRecipe;
    private String tuesdayRecipe;
    private String wednesdayRecipe;
    private String thursdayRecipe;
    private String fridayRecipe;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Analytics
    private Long viewCount;
    private Long joinCount;
    private Double engagementRate;
    private Double avgRating;
}
