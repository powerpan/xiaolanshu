package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class NutritionRecommendation {
    private String goal;
    private Double weightKg;
    private Integer weeklyFrequency;
    private Integer targetCalories;
    private Integer proteinGrams;
    private Integer carbohydrateGrams;
    private Integer fatGrams;
    private Integer waterMl;
    private String summary;
    private String trainingDayTip;
    private String restDayTip;
    private ArrayList<String> meals;
    private ArrayList<String> tips;
    private ArrayList<String> mealTiming;
    private ArrayList<String> foodChoices;
    private ArrayList<String> watchouts;
}
