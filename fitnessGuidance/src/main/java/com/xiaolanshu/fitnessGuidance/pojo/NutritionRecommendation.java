package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class NutritionRecommendation {
    private Integer targetCalories;
    private Integer proteinGrams;
    private Integer waterMl;
    private String summary;
    private ArrayList<String> meals;
    private ArrayList<String> tips;
}
