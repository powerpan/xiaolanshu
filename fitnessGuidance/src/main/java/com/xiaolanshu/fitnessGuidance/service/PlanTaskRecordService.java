package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.PlanTaskRecord;

import java.util.ArrayList;

public interface PlanTaskRecordService {
    ArrayList<PlanTaskRecord> listToday(String username, Integer daytime);

    void saveToday(String username, PlanTaskRecord record);

    Integer countThisWeek(String username);

    Integer countCompletedThisWeek(String username);
}
