package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PlanAdjustmentRecord {
    private Integer id;
    private String username;
    private String reason;
    private String adjustmentType;
    private String summary;
    private Timestamp createdAt;
}
