package com.mealplan.service;

import com.mealplan.dto.CreateMealPlanRequest;
import com.mealplan.dto.MealPlanDto;
import com.mealplan.entity.MealPlan;
import com.mealplan.entity.User;
import com.mealplan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserService userService;
    private final MealPlanJoinRepository joinRepository;
    private final PlanViewRepository viewRepository;
    private final PlanRatingRepository ratingRepository;

    public MealPlanDto createMealPlan(CreateMealPlanRequest request, UUID creatorId) {
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
        return toDto(saved);
    }

    public MealPlanDto getMealPlanById(UUID id) {
        MealPlan plan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));
        return toDto(plan);
    }

    public Page<MealPlanDto> getAllMealPlans(Pageable pageable) {
        return mealPlanRepository.findAll(pageable)
                .map(this::toDto);
    }

    public Page<MealPlanDto> filterMealPlans(String cuisine, String diet, Pageable pageable) {
        return mealPlanRepository.findByFilters(cuisine, diet, pageable)
                .map(this::toDto);
    }

    public Page<MealPlanDto> getCreatorPlans(UUID creatorId, Pageable pageable) {
        User creator = userService.getUserById(creatorId);
        return mealPlanRepository.findByCreator(creator, pageable)
                .map(this::toDto);
    }

    public MealPlanDto updateMealPlan(UUID planId, CreateMealPlanRequest request, UUID creatorId) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));

        if (!plan.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("Unauthorized");
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
        return toDto(updated);
    }

    public void deleteMealPlan(UUID planId, UUID creatorId) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));

        if (!plan.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("Unauthorized");
        }

        mealPlanRepository.deleteById(planId);
    }

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