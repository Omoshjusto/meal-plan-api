package com.mealplan.repository;

import com.mealplan.entity.MealPlanJoin;
import com.mealplan.entity.User;
import com.mealplan.entity.MealPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface MealPlanJoinRepository extends JpaRepository<MealPlanJoin, UUID> {

    Optional<MealPlanJoin> findByUserAndMealPlan(User user, MealPlan mealPlan);

    Page<MealPlanJoin> findByUser(User user, Pageable pageable);

    Page<MealPlanJoin> findByMealPlan(MealPlan mealPlan, Pageable pageable);

    List<MealPlanJoin> findByMealPlan(MealPlan mealPlan);

    long countByMealPlan(MealPlan mealPlan);

    boolean existsByUserAndMealPlan(User user, MealPlan mealPlan);
}