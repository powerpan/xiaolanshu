package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class NutritionLibrary {
    private ArrayList<FoodItem> foodItems;
    private ArrayList<MealTemplate> mealTemplates;
    private ArrayList<FoodReplacement> foodReplacements;
    private ArrayList<EatingScenario> eatingScenarios;
}
