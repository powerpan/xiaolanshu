package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class MealTemplate {
    private Integer id;
    private String name;
    private String mealType;
    private String goal;
    private String scene;
    private Integer targetCalories;
    private String description;
    private String foods;
}
