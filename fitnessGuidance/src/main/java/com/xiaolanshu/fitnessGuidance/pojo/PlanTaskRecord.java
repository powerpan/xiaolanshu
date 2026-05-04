package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlanTaskRecord {
    private Integer id;
    private String username;
    private LocalDate planDate;
    private Integer daytime;
    private Integer actionIndex;
    private String actionPattern;
    private String actionName;
    private String equipment;
    private Boolean completed;
    private Integer actualSets;
    private Integer actualReps;
    private Integer difficultyScore;
    private String note;
    private LocalDateTime updatedAt;
}
