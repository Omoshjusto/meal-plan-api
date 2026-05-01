package com.mealplan.controller;

import com.mealplan.dto.CreateMealPlanRequest;
import com.mealplan.dto.MealPlanDto;
import com.mealplan.service.MealPlanService;
import com.mealplan.service.MealPlanJoinService;
import com.mealplan.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final MealPlanJoinService joinService;
    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<Page<MealPlanDto>> getAllPlans(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String diet,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        if (cuisine != null || diet != null) {
            return ResponseEntity.ok(mealPlanService.filterMealPlans(cuisine, diet, pageable));
        }

        return ResponseEntity.ok(mealPlanService.getAllMealPlans(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealPlanDto> getMealPlan(@PathVariable UUID id) {
        // Log view when plan is viewed
        analyticsService.logPlanView(id);

        MealPlanDto plan = mealPlanService.getMealPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @PostMapping
    public ResponseEntity<MealPlanDto> createMealPlan(
            @Valid @RequestBody CreateMealPlanRequest request,
            @RequestParam UUID creatorId
    ) {
        MealPlanDto plan = mealPlanService.createMealPlan(request, creatorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealPlanDto> updateMealPlan(
            @PathVariable UUID id,
            @Valid @RequestBody CreateMealPlanRequest request,
            @RequestParam UUID creatorId
    ) {
        MealPlanDto plan = mealPlanService.updateMealPlan(id, request, creatorId);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealPlan(
            @PathVariable UUID id,
            @RequestParam UUID creatorId
    ) {
        mealPlanService.deleteMealPlan(id, creatorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<String> joinMealPlan(
            @PathVariable UUID id,
            @RequestParam UUID userId
    ) {
        joinService.joinMealPlan(userId, id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Joined plan successfully");
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveMealPlan(
            @PathVariable UUID id,
            @RequestParam UUID userId
    ) {
        joinService.leaveMealPlan(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<Page<String>> getMealPlanMembers(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<String> members = joinService.getJoinedPlansByUser(id, pageable);
        return ResponseEntity.ok(members);
    }
}
