package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class FoodItem {
    private Integer id;
    private String name;
    private String category;
    private Integer caloriesPer100g;
    private Double proteinPer100g;
    private Double carbohydratePer100g;
    private Double fatPer100g;
    private String tags;
    private String scene;
}
