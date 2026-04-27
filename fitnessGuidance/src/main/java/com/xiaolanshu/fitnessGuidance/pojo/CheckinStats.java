package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CheckinStats {
    private Integer totalDays;
    private Integer currentStreak;
    private Integer monthDays;
    private Integer totalMinutes;
    private ArrayList<FitnessCheckin> recentCheckins;
}
