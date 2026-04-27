package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.FitnessCheckin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.ArrayList;

@Mapper
public interface FitnessCheckinMapper {

    @Select("SELECT * FROM fitnesscheckins WHERE username = #{username} AND checkin_date = #{checkinDate}")
    FitnessCheckin findByDate(String username, LocalDate checkinDate);

    @Insert("MERGE INTO fitnesscheckins " +
            "(username, checkin_date, duration_minutes, mood, note, created_at) " +
            "KEY(username, checkin_date) " +
            "VALUES(#{username}, #{checkinDate}, #{durationMinutes}, #{mood}, #{note}, NOW())")
    void saveToday(String username, LocalDate checkinDate, Integer durationMinutes, String mood, String note);

    @Select("SELECT * FROM fitnesscheckins WHERE username = #{username} ORDER BY checkin_date DESC LIMIT #{limit}")
    ArrayList<FitnessCheckin> recent(String username, Integer limit);

    @Select("SELECT COUNT(*) FROM fitnesscheckins WHERE username = #{username}")
    Integer countAll(String username);

    @Select("SELECT COALESCE(SUM(duration_minutes), 0) FROM fitnesscheckins WHERE username = #{username}")
    Integer sumMinutes(String username);

    @Select("SELECT COUNT(*) FROM fitnesscheckins " +
            "WHERE username = #{username} AND YEAR(checkin_date) = YEAR(CURRENT_DATE) " +
            "AND MONTH(checkin_date) = MONTH(CURRENT_DATE)")
    Integer countThisMonth(String username);

    @Select("SELECT checkin_date FROM fitnesscheckins WHERE username = #{username} ORDER BY checkin_date DESC")
    ArrayList<LocalDate> allDatesDesc(String username);
}
