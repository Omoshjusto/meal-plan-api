package com.mealplan.service;

import com.mealplan.entity.MealPlan;
import com.mealplan.entity.MealPlanJoin;
import com.mealplan.entity.User;
import com.mealplan.repository.MealPlanJoinRepository;
import com.mealplan.repository.MealPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealPlanJoinService {

    private final MealPlanJoinRepository joinRepository;
    private final MealPlanRepository mealPlanRepository;
    private final UserService userService;

    public void joinMealPlan(UUID userId, UUID mealPlanId) {
        User user = userService.getUserById(userId);
        MealPlan plan = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));

        if (joinRepository.existsByUserAndMealPlan(user, plan)) {
            throw new IllegalArgumentException("User already joined this plan");
        }

        MealPlanJoin join = MealPlanJoin.builder()
                .user(user)
                .mealPlan(plan)
                .build();

        joinRepository.save(join);
    }

    public void leaveMealPlan(UUID userId, UUID mealPlanId) {
        User user = userService.getUserById(userId);
        MealPlan plan = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));

        MealPlanJoin join = joinRepository.findByUserAndMealPlan(user, plan)
                .orElseThrow(() -> new IllegalArgumentException("User not joined this plan"));

        joinRepository.delete(join);
    }

    public Page<String> getJoinedPlansByUser(UUID userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        return joinRepository.findByUser(user, pageable)
                .map(j -> j.getMealPlan().getId().toString());
    }

    public boolean isUserJoined(UUID userId, UUID mealPlanId) {
        User user = userService.getUserById(userId);
        MealPlan plan = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Meal plan not found"));

        return joinRepository.existsByUserAndMealPlan(user, plan);
    }
}