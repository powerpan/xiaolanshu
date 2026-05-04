package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.FitnessCheckin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.ArrayList;

@Mapper
public interface FitnessCheckinMapper {

    @Select("SELECT id, username, checkin_date AS \"checkinDate\", plan_day AS \"planDay\", " +
            "duration_minutes AS \"durationMinutes\", mood, note, created_at AS \"createdAt\" " +
            "FROM fitnesscheckins WHERE username = #{username} AND checkin_date = #{checkinDate}")
    FitnessCheckin findByDate(@Param("username") String username, @Param("checkinDate") LocalDate checkinDate);

    @Update("UPDATE fitnesscheckins SET plan_day = COALESCE(#{planDay}, plan_day), duration_minutes = #{durationMinutes}, mood = #{mood}, " +
            "note = #{note}, created_at = CURRENT_TIMESTAMP " +
            "WHERE username = #{username} AND checkin_date = #{checkinDate}")
    Integer updateToday(@Param("username") String username,
                        @Param("checkinDate") LocalDate checkinDate,
                        @Param("planDay") Integer planDay,
                        @Param("durationMinutes") Integer durationMinutes,
                        @Param("mood") String mood,
                        @Param("note") String note);

    @Insert("INSERT INTO fitnesscheckins " +
            "(username, checkin_date, plan_day, duration_minutes, mood, note, created_at) " +
            "VALUES(#{username}, #{checkinDate}, #{planDay}, #{durationMinutes}, #{mood}, #{note}, CURRENT_TIMESTAMP)")
    void insertToday(@Param("username") String username,
                     @Param("checkinDate") LocalDate checkinDate,
                     @Param("planDay") Integer planDay,
                     @Param("durationMinutes") Integer durationMinutes,
                     @Param("mood") String mood,
                     @Param("note") String note);

    @Select("SELECT id, username, checkin_date AS \"checkinDate\", plan_day AS \"planDay\", " +
            "duration_minutes AS \"durationMinutes\", mood, note, created_at AS \"createdAt\" " +
            "FROM fitnesscheckins WHERE username = #{username} ORDER BY checkin_date DESC LIMIT #{limit}")
    ArrayList<FitnessCheckin> recent(@Param("username") String username, @Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM fitnesscheckins WHERE username = #{username} AND plan_day = #{planDay}")
    Integer countByPlanDay(@Param("username") String username, @Param("planDay") Integer planDay);

    @Select("SELECT COUNT(*) FROM fitnesscheckins WHERE username = #{username}")
    Integer countAll(String username);

    @Select("SELECT COALESCE(SUM(duration_minutes), 0) FROM fitnesscheckins WHERE username = #{username}")
    Integer sumMinutes(String username);

    @Select("SELECT COUNT(*) FROM fitnesscheckins " +
            "WHERE username = #{username} AND checkin_date >= #{monthStart} AND checkin_date < #{nextMonthStart}")
    Integer countInDateRange(@Param("username") String username,
                             @Param("monthStart") LocalDate monthStart,
                             @Param("nextMonthStart") LocalDate nextMonthStart);

    @Select("SELECT checkin_date FROM fitnesscheckins WHERE username = #{username} ORDER BY checkin_date DESC")
    ArrayList<LocalDate> allDatesDesc(String username);
}
