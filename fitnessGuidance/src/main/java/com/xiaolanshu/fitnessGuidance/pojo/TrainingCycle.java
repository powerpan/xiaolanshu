package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingCycle {
    private Integer id;
    private String username;
    private Integer cycleWeek;
    private String stage;
    private String cycleGoal;
    private Integer recoveryDay;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
