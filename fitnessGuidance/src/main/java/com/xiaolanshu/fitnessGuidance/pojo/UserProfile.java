package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class UserProfile {
    private Integer id;
    private String username;
    private String fitnessGoal;
    private Integer weeklyFrequency;
    private String equipment;
    private String exLevel;
}
