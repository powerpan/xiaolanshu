package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.FitnessCheckinMapper;
import com.xiaolanshu.fitnessGuidance.pojo.CheckinStats;
import com.xiaolanshu.fitnessGuidance.pojo.FitnessCheckin;
import com.xiaolanshu.fitnessGuidance.service.FitnessCheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class FitnessCheckinServiceImpl implements FitnessCheckinService {

    @Autowired
    private FitnessCheckinMapper fitnessCheckinMapper;

    @Override
    public FitnessCheckin getToday(String username) {
        return fitnessCheckinMapper.findByDate(username, LocalDate.now());
    }

    @Override
    public void saveToday(String username, Integer durationMinutes, String mood, String note) {
        Integer safeDuration = durationMinutes == null || durationMinutes < 0 ? 0 : durationMinutes;
        fitnessCheckinMapper.saveToday(username, LocalDate.now(), safeDuration, mood, note);
    }

    @Override
    public CheckinStats getStats(String username) {
        CheckinStats stats = new CheckinStats();
        stats.setTotalDays(fitnessCheckinMapper.countAll(username));
        stats.setTotalMinutes(fitnessCheckinMapper.sumMinutes(username));
        stats.setMonthDays(fitnessCheckinMapper.countThisMonth(username));
        stats.setRecentCheckins(fitnessCheckinMapper.recent(username, 8));
        stats.setCurrentStreak(calculateStreak(fitnessCheckinMapper.allDatesDesc(username)));
        return stats;
    }

    private Integer calculateStreak(ArrayList<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            return 0;
        }

        LocalDate cursor = LocalDate.now();
        int streak = 0;

        for (LocalDate date : dates) {
            if (date == null) {
                continue;
            }
            if (date.equals(cursor)) {
                streak++;
                cursor = cursor.minusDays(1);
            } else if (date.equals(cursor.plusDays(1)) && streak == 0) {
                continue;
            } else if (date.isBefore(cursor)) {
                break;
            }
        }

        return streak;
    }
}
