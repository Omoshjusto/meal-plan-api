package com.mealplan.exception;

public class MealPlanNotFoundException extends RuntimeException {
    public MealPlanNotFoundException(String message) {
        super(message);
    }

    public MealPlanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}