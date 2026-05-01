package com.mealplan.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMealPlanRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String cuisine;
    private String diet;

    private String mondayRecipe;
    private String tuesdayRecipe;
    private String wednesdayRecipe;
    private String thursdayRecipe;
    private String fridayRecipe;
}
