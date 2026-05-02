package com.mealplan.service;

import com.mealplan.dto.CreateMealPlanRequest;
import com.mealplan.dto.MealPlanDto;
import com.mealplan.entity.MealPlan;
import com.mealplan.entity.User;
import com.mealplan.exception.MealPlanNotFoundException;
import com.mealplan.exception.UnauthorizedException;
import com.mealplan.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserService userService;
    private final MealPlanJoinRepository joinRepository;
    private final PlanViewRepository viewRepository;
    private final PlanRatingRepository ratingRepository;

    /**
     * Create a new meal plan
     */
    public MealPlanDto createMealPlan(CreateMealPlanRequest request, UUID creatorId) {
        log.info("Creating meal plan for user: {}", creatorId);

        User creator = userService.getUserById(creatorId);

        MealPlan mealPlan = MealPlan.builder()
                .creator(creator)
                .title(request.getTitle())
                .description(request.getDescription())
                .cuisine(request.getCuisine())
                .diet(request.getDiet())
                .mondayRecipe(request.getMondayRecipe())
                .tuesdayRecipe(request.getTuesdayRecipe())
                .wednesdayRecipe(request.getWednesdayRecipe())
                .thursdayRecipe(request.getThursdayRecipe())
                .fridayRecipe(request.getFridayRecipe())
                .build();

        MealPlan saved = mealPlanRepository.save(mealPlan);
        log.info("Meal plan created successfully: {}", saved.getId());
        return toDto(saved);
    }

    /**
     * Get meal plan by ID
     */
    public MealPlanDto getMealPlanById(UUID id) {
        MealPlan plan = mealPlanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Meal plan not found: {}", id);
                    return new MealPlanNotFoundException("Meal plan not found with id: " + id);
                });
        return toDto(plan);
    }

    /**
     * Get all meal plans (paginated)
     */
    public Page<MealPlanDto> getAllMealPlans(Pageable pageable) {
        log.debug("Fetching all meal plans with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return mealPlanRepository.findAll(pageable)
                .map(this::toDto);
    }

    /**
     * Filter meal plans by cuisine and/or diet
     */
    public Page<MealPlanDto> filterMealPlans(String cuisine, String diet, Pageable pageable) {
        log.debug("Filtering meal plans: cuisine={}, diet={}", cuisine, diet);
        return mealPlanRepository.findByFilters(cuisine, diet, pageable)
                .map(this::toDto);
    }

    /**
     * Get all plans created by a specific creator
     */
    public Page<MealPlanDto> getCreatorPlans(UUID creatorId, Pageable pageable) {
        User creator = userService.getUserById(creatorId);
        log.debug("Fetching plans for creator: {}", creatorId);
        return mealPlanRepository.findByCreator(creator, pageable)
                .map(this::toDto);
    }

    /**
     * Update a meal plan
     */
    public MealPlanDto updateMealPlan(UUID planId, CreateMealPlanRequest request, UUID creatorId) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> {
                    log.warn("Meal plan not found: {}", planId);
                    return new MealPlanNotFoundException("Meal plan not found with id: " + planId);
                });

        // Check authorization
        if (!plan.getCreator().getId().equals(creatorId)) {
            log.warn("Unauthorized update attempt: user={}, plan={}", creatorId, planId);
            throw new UnauthorizedException("You are not authorized to update this meal plan");
        }

        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setCuisine(request.getCuisine());
        plan.setDiet(request.getDiet());
        plan.setMondayRecipe(request.getMondayRecipe());
        plan.setTuesdayRecipe(request.getTuesdayRecipe());
        plan.setWednesdayRecipe(request.getWednesdayRecipe());
        plan.setThursdayRecipe(request.getThursdayRecipe());
        plan.setFridayRecipe(request.getFridayRecipe());

        MealPlan updated = mealPlanRepository.save(plan);
        log.info("Meal plan updated: {}", planId);
        return toDto(updated);
    }

    /**
     * Delete a meal plan
     */
    public void deleteMealPlan(UUID planId, UUID creatorId) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> {
                    log.warn("Meal plan not found: {}", planId);
                    return new MealPlanNotFoundException("Meal plan not found with id: " + planId);
                });

        // Check authorization
        if (!plan.getCreator().getId().equals(creatorId)) {
            log.warn("Unauthorized delete attempt: user={}, plan={}", creatorId, planId);
            throw new UnauthorizedException("You are not authorized to delete this meal plan");
        }

        mealPlanRepository.deleteById(planId);
        log.info("Meal plan deleted: {}", planId);
    }

    /**
     * Convert MealPlan entity to MealPlanDto with analytics
     */
    private MealPlanDto toDto(MealPlan plan) {
        long viewCount = viewRepository.countByMealPlan(plan);
        long joinCount = joinRepository.countByMealPlan(plan);
        Double avgRating = ratingRepository.findAverageRatingByMealPlan(plan);

        double engagementRate = viewCount > 0 ? (double) joinCount / viewCount : 0.0;

        return MealPlanDto.builder()
                .id(plan.getId())
                .creatorId(plan.getCreator().getId())
                .creatorUsername(plan.getCreator().getUsername())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .cuisine(plan.getCuisine())
                .diet(plan.getDiet())
                .mondayRecipe(plan.getMondayRecipe())
                .tuesdayRecipe(plan.getTuesdayRecipe())
                .wednesdayRecipe(plan.getWednesdayRecipe())
                .thursdayRecipe(plan.getThursdayRecipe())
                .fridayRecipe(plan.getFridayRecipe())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .viewCount(viewCount)
                .joinCount(joinCount)
                .engagementRate(engagementRate)
                .avgRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null)
                .build();
    }
}