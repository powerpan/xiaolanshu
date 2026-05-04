package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.TrainingCycle;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;

@Mapper
public interface TrainingCycleMapper {

    @Select("SELECT id, username, cycle_week AS \"cycleWeek\", stage, cycle_goal AS \"cycleGoal\", " +
            "recovery_day AS \"recoveryDay\", start_date AS \"startDate\", end_date AS \"endDate\", status " +
            "FROM trainingcycles WHERE username = #{username} AND status = 'active' ORDER BY id DESC LIMIT 1")
    TrainingCycle current(String username);

    @Insert("INSERT INTO trainingcycles(username, cycle_week, stage, cycle_goal, recovery_day, start_date, end_date, status) " +
            "VALUES(#{username}, #{cycleWeek}, #{stage}, #{cycleGoal}, #{recoveryDay}, #{startDate}, #{endDate}, 'active')")
    void insert(TrainingCycle cycle);

    @Update("UPDATE trainingcycles SET cycle_week = #{cycleWeek}, stage = #{stage}, cycle_goal = #{cycleGoal}, " +
            "recovery_day = #{recoveryDay}, start_date = #{startDate}, end_date = #{endDate}, status = #{status} " +
            "WHERE id = #{id} AND username = #{username}")
    void update(TrainingCycle cycle);

    @Select("SELECT COUNT(*) FROM fitnesscheckins WHERE username = #{username} AND checkin_date >= #{startDate}")
    Integer countCheckinsSince(@Param("username") String username, @Param("startDate") LocalDate startDate);
}
