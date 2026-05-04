package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class FoodReplacement {
    private Integer id;
    private String sourceFood;
    private String replacementFood;
    private String reason;
    private String category;
}
