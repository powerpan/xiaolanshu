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

    @Select("SELECT * FROM fitnesscheckins WHERE username = #{username} AND checkin_date = #{checkinDate}")
    FitnessCheckin findByDate(@Param("username") String username, @Param("checkinDate") LocalDate checkinDate);

    @Update("UPDATE fitnesscheckins SET duration_minutes = #{durationMinutes}, mood = #{mood}, " +
            "note = #{note}, created_at = CURRENT_TIMESTAMP " +
            "WHERE username = #{username} AND checkin_date = #{checkinDate}")
    Integer updateToday(@Param("username") String username,
                        @Param("checkinDate") LocalDate checkinDate,
                        @Param("durationMinutes") Integer durationMinutes,
                        @Param("mood") String mood,
                        @Param("note") String note);

    @Insert("INSERT INTO fitnesscheckins " +
            "(username, checkin_date, duration_minutes, mood, note, created_at) " +
            "VALUES(#{username}, #{checkinDate}, #{durationMinutes}, #{mood}, #{note}, CURRENT_TIMESTAMP)")
    void insertToday(@Param("username") String username,
                     @Param("checkinDate") LocalDate checkinDate,
                     @Param("durationMinutes") Integer durationMinutes,
                     @Param("mood") String mood,
                     @Param("note") String note);

    @Select("SELECT * FROM fitnesscheckins WHERE username = #{username} ORDER BY checkin_date DESC LIMIT #{limit}")
    ArrayList<FitnessCheckin> recent(@Param("username") String username, @Param("limit") Integer limit);

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
