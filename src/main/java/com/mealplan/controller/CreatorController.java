package com.mealplan.controller;

import com.mealplan.dto.CreatorAnalyticsDto;
import com.mealplan.dto.MealPlanDto;
import com.mealplan.service.MealPlanService;
import com.mealplan.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/creators")
@RequiredArgsConstructor
public class CreatorController {

    private final MealPlanService mealPlanService;
    private final AnalyticsService analyticsService;

    @GetMapping("/{creatorId}/plans")
    public ResponseEntity<Page<MealPlanDto>> getCreatorPlans(
            @PathVariable UUID creatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MealPlanDto> plans = mealPlanService.getCreatorPlans(creatorId, pageable);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{creatorId}/plans/{planId}/analytics")
    public ResponseEntity<CreatorAnalyticsDto> getPlanAnalytics(
            @PathVariable UUID creatorId,
            @PathVariable UUID planId
    ) {
        CreatorAnalyticsDto analytics = analyticsService.getAnalyticsForPlan(planId, creatorId);
        return ResponseEntity.ok(analytics);
    }
}
