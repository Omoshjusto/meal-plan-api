package com.mealplan.repository;

import com.mealplan.entity.PlanView;
import com.mealplan.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanViewRepository extends JpaRepository<PlanView, UUID> {

    long countByMealPlan(MealPlan mealPlan);

    @Query("SELECT COUNT(DISTINCT pv.user) FROM PlanView pv WHERE pv.mealPlan = :mealPlan")
    long countUniqueViewsByMealPlan(@Param("mealPlan") MealPlan mealPlan);

    @Query("SELECT pv FROM PlanView pv WHERE pv.mealPlan = :mealPlan " +
            "ORDER BY pv.viewedAt DESC")
    List<PlanView> findByMealPlanOrderByViewedAtDesc(@Param("mealPlan") MealPlan mealPlan);

    @Query("SELECT pv FROM PlanView pv WHERE pv.mealPlan = :mealPlan " +
            "AND pv.viewedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY pv.viewedAt DESC")
    List<PlanView> findByMealPlanAndDateRange(
            @Param("mealPlan") MealPlan mealPlan,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
