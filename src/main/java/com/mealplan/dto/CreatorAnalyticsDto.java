package com.mealplan.dto;

import lombok.*;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatorAnalyticsDto {

    private UUID planId;
    private String planTitle;

    private Long viewCount;
    private Long uniqueViewCount;
    private Long joinCount;
    private Double engagementRate;
    private Double avgRating;

    private List<MemberDto> members;
    private List<DailyMetricDto> viewTrend;
    private List<DailyMetricDto> joinTrend;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberDto {
        private UUID id;
        private String username;
        private String email;
        private java.time.LocalDateTime joinedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyMetricDto {
        private String date;
        private Long count;
    }
}
