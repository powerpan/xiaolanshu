package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.PlanTaskRecordMapper;
import com.xiaolanshu.fitnessGuidance.pojo.PlanTaskRecord;
import com.xiaolanshu.fitnessGuidance.service.PlanTaskRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class PlanTaskRecordServiceImpl implements PlanTaskRecordService {

    @Autowired
    private PlanTaskRecordMapper planTaskRecordMapper;

    @Override
    public ArrayList<PlanTaskRecord> listToday(String username, Integer daytime) {
        return planTaskRecordMapper.listByDay(username, LocalDate.now(), daytime == null ? 1 : daytime);
    }

    @Override
    public void saveToday(String username, PlanTaskRecord record) {
        record.setUsername(username);
        record.setPlanDate(LocalDate.now());
        if (record.getDaytime() == null) {
            record.setDaytime(1);
        }
        if (record.getActionIndex() == null) {
            record.setActionIndex(0);
        }
        if (record.getCompleted() == null) {
            record.setCompleted(false);
        }
        Integer updated = planTaskRecordMapper.updateByKey(record);
        if (updated == null || updated == 0) {
            planTaskRecordMapper.insert(record);
        }
    }

    @Override
    public Integer countThisWeek(String username) {
        LocalDate start = weekStart();
        return planTaskRecordMapper.countInRange(username, start, start.plusDays(7));
    }

    @Override
    public Integer countCompletedThisWeek(String username) {
        LocalDate start = weekStart();
        return planTaskRecordMapper.countCompletedInRange(username, start, start.plusDays(7));
    }

    private LocalDate weekStart() {
        LocalDate today = LocalDate.now();
        return today.minusDays(today.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }
}
