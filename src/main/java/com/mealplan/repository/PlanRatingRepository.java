package com.mealplan.repository;

import com.mealplan.entity.PlanRating;
import com.mealplan.entity.MealPlan;
import com.mealplan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface PlanRatingRepository extends JpaRepository<PlanRating, UUID> {

    Optional<PlanRating> findByUserAndMealPlan(User user, MealPlan mealPlan);

    List<PlanRating> findByMealPlan(MealPlan mealPlan);

    @Query("SELECT AVG(pr.rating) FROM PlanRating pr WHERE pr.mealPlan = :mealPlan")
    Double findAverageRatingByMealPlan(@Param("mealPlan") MealPlan mealPlan);

    long countByMealPlan(MealPlan mealPlan);
}