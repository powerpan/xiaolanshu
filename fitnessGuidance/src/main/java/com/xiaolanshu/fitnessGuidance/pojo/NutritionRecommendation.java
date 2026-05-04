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
    private String preferenceSummary;
    private String trainingDayTip;
    private String restDayTip;
    private ArrayList<String> meals;
    private ArrayList<String> trainingDayMeals;
    private ArrayList<String> restDayMeals;
    private ArrayList<String> tips;
    private ArrayList<String> mealTiming;
    private ArrayList<String> foodChoices;
    private ArrayList<String> replacements;
    private ArrayList<String> shoppingList;
    private ArrayList<String> watchouts;
    private ArrayList<String> mealTemplates;
    private ArrayList<String> eatingScenarios;
    private ArrayList<String> foodReplacements;
}
