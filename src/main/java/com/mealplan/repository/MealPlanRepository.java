package com.mealplan.repository;

import com.mealplan.entity.MealPlan;
import com.mealplan.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {

    Page<MealPlan> findByCreator(User creator, Pageable pageable);

    @Query("SELECT mp FROM MealPlan mp WHERE " +
            "(:cuisine IS NULL OR mp.cuisine = :cuisine) AND " +
            "(:diet IS NULL OR mp.diet = :diet)")
    Page<MealPlan> findByFilters(
            @Param("cuisine") String cuisine,
            @Param("diet") String diet,
            Pageable pageable
    );

    Page<MealPlan> findAll(Pageable pageable);
}