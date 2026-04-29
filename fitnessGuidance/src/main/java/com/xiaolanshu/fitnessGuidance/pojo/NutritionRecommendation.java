package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class NutritionRecommendation {
    private Integer targetCalories;
    private Integer proteinGrams;
    private Integer carbohydrateGrams;
    private Integer fatGrams;
    private Integer waterMl;
    private String summary;
    private String trainingDayTip;
    private ArrayList<String> meals;
    private ArrayList<String> tips;
}
