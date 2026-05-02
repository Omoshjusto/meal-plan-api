package com.mealplan.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private String path;
}