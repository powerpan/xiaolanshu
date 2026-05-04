package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class NutritionPreference {
    private Integer id;
    private String username;
    private String dietType;
    private String allergies;
    private String budgetLevel;
    private String eatingOutFrequency;
    private Integer mealCount;
    private String tastePreference;
}
