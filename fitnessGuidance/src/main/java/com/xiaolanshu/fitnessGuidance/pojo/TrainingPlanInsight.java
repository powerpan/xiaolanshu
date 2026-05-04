package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TrainingPlanInsight {
    private Integer weeklyTarget;
    private Integer cycleWeek;
    private String stage;
    private String cycleGoal;
    private Integer recoveryDay;
    private Integer recordedTasks;
    private Integer completedTasks;
    private Integer completionRate;
    private Integer currentStreak;
    private String recommendation;
    private String recoveryHint;
    private String nextAdjustment;
    private ArrayList<PlanAdjustmentRecord> adjustmentHistory;
}
