package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.CheckinStats;
import com.xiaolanshu.fitnessGuidance.pojo.FitnessCheckin;

public interface FitnessCheckinService {
    FitnessCheckin getToday(String username);

    void saveToday(String username, Integer durationMinutes, String mood, String note);

    void saveToday(String username, Integer durationMinutes, String mood, String note, Integer planDay);

    CheckinStats getStats(String username);
}
