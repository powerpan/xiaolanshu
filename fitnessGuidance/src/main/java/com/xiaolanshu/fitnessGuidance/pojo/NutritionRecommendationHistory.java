package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NutritionRecommendationHistory {
    private Integer id;
    private String username;
    private Integer targetCalories;
    private Integer proteinGrams;
    private Integer carbohydrateGrams;
    private Integer fatGrams;
    private String preferenceSummary;
    private String summary;
    private LocalDateTime createdAt;
}
