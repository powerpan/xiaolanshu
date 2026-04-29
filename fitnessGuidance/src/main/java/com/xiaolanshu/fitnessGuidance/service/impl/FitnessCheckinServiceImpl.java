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
        LocalDate today = LocalDate.now();
        Integer updatedRows = fitnessCheckinMapper.updateToday(username, today, safeDuration, mood, note);
        if (updatedRows == null || updatedRows == 0) {
            fitnessCheckinMapper.insertToday(username, today, safeDuration, mood, note);
        }
    }

    @Override
    public CheckinStats getStats(String username) {
        CheckinStats stats = new CheckinStats();
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate nextMonthStart = monthStart.plusMonths(1);
        Integer totalDays = fitnessCheckinMapper.countAll(username);
        Integer totalMinutes = fitnessCheckinMapper.sumMinutes(username);
        stats.setTotalDays(totalDays);
        stats.setTotalMinutes(totalMinutes);
        stats.setAverageMinutes(totalDays == null || totalDays == 0 ? 0 : totalMinutes / totalDays);
        stats.setMonthDays(fitnessCheckinMapper.countInDateRange(username, monthStart, nextMonthStart));
        stats.setRecentCheckins(fitnessCheckinMapper.recent(username, 42));
        ArrayList<LocalDate> dates = fitnessCheckinMapper.allDatesDesc(username);
        stats.setCurrentStreak(calculateStreak(dates));
        stats.setBestStreak(calculateBestStreak(dates));
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

    private Integer calculateBestStreak(ArrayList<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            return 0;
        }

        int best = 0;
        int current = 0;
        LocalDate previous = null;

        for (int i = dates.size() - 1; i >= 0; i--) {
            LocalDate date = dates.get(i);
            if (date == null) {
                continue;
            }
            if (previous == null || date.equals(previous.plusDays(1))) {
                current++;
            } else if (!date.equals(previous)) {
                current = 1;
            }
            best = Math.max(best, current);
            previous = date;
        }

        return best;
    }
}
