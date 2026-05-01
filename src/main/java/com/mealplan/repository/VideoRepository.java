package com.mealplan.repository;

import com.mealplan.entity.Video;
import com.mealplan.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    List<Video> findByMealPlan(MealPlan mealPlan);
}