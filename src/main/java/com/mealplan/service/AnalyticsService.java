package com.mealplan.service;

import com.mealplan.dto.CreatorAnalyticsDto;
import com.mealplan.entity.MealPlan;
import com.mealplan.entity.PlanView;
import com.mealplan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final PlanViewRepository viewRepository;
    private final MealPlanJoinRepository joinRepository;
    private final PlanRatingRepository ratingRepository;
    private final MealPlanRepository mealPlanRepository;
    private final UserService userService;

    public CreatorAnalyticsDto getAnalyticsForPlan(UUID planId, UUID creatorId) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        if (!plan.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("Unauthorized");
        }

        long viewCount = viewRepository.countByMealPlan(plan);
        long uniqueViews = viewRepository.countUniqueViewsByMealPlan(plan);
        long joinCount = joinRepository.countByMealPlan(plan);
        Double avgRating = ratingRepository.findAverageRatingByMealPlan(plan);

        double engagementRate = viewCount > 0 ? (double) joinCount / viewCount : 0.0;

        // Get members who joined
        List<CreatorAnalyticsDto.MemberDto> members = joinRepository.findByMealPlan(plan)
                .stream()
                .map(join -> CreatorAnalyticsDto.MemberDto.builder()
                        .id(join.getUser().getId())
                        .username(join.getUser().getUsername())
                        .email(join.getUser().getEmail())
                        .joinedAt(join.getJoinedAt())
                        .build())
                .collect(Collectors.toList());

        // Calculate trends (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<PlanView> recentViews = viewRepository.findByMealPlanAndDateRange(
                plan, thirtyDaysAgo, LocalDateTime.now()
        );

        List<CreatorAnalyticsDto.DailyMetricDto> viewTrend = groupByDay(recentViews);

        return CreatorAnalyticsDto.builder()
                .planId(plan.getId())
                .planTitle(plan.getTitle())
                .viewCount(viewCount)
                .uniqueViewCount(uniqueViews)
                .joinCount(joinCount)
                .engagementRate(engagementRate)
                .avgRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null)
                .members(members)
                .viewTrend(viewTrend)
                .build();
    }

    public void logPlanView(UUID planId) {
        MealPlan plan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        PlanView view = PlanView.builder()
                .mealPlan(plan)
                .user(null) // Anonymous view for now
                .build();

        viewRepository.save(view);
    }

    private List<CreatorAnalyticsDto.DailyMetricDto> groupByDay(List<PlanView> views) {
        return views.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getViewedAt().toLocalDate().toString(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> CreatorAnalyticsDto.DailyMetricDto.builder()
                        .date(e.getKey())
                        .count(e.getValue())
                        .build())
                .sorted(Comparator.comparing(CreatorAnalyticsDto.DailyMetricDto::getDate))
                .collect(Collectors.toList());
    }
}
