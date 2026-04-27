package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FitnessPlanRule {
    private Integer id;
    private String fitnessGoal;       // 健身目标
    private Integer weeklyFrequency;   // 每周频率
    private ArrayList<String> splitMode;    // 训练分化模式
}
