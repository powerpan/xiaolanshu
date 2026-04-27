package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FitnessCheckin {
    private Integer id;
    private String username;
    private LocalDate checkinDate;
    private Integer durationMinutes;
    private String mood;
    private String note;
    private LocalDateTime createdAt;
}
