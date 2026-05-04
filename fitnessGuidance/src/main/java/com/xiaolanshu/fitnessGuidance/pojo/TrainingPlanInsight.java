package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class TrainingPlanInsight {
    private Integer weeklyTarget;
    private Integer recordedTasks;
    private Integer completedTasks;
    private Integer completionRate;
    private Integer currentStreak;
    private String recommendation;
    private String recoveryHint;
    private String nextAdjustment;
}
