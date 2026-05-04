package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.PlanTaskRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.ArrayList;

@Mapper
public interface PlanTaskRecordMapper {

    @Select("SELECT id, username, plan_date AS \"planDate\", daytime, action_index AS \"actionIndex\", " +
            "actionpattern AS \"actionPattern\", actionname AS \"actionName\", equipment, completed, " +
            "actual_sets AS \"actualSets\", actual_reps AS \"actualReps\", difficulty_score AS \"difficultyScore\", " +
            "note, updated_at AS \"updatedAt\" FROM plantaskrecords " +
            "WHERE username = #{username} AND plan_date = #{planDate} AND daytime = #{daytime} " +
            "ORDER BY action_index")
    ArrayList<PlanTaskRecord> listByDay(@Param("username") String username,
                                        @Param("planDate") LocalDate planDate,
                                        @Param("daytime") Integer daytime);

    @Update("UPDATE plantaskrecords SET actionpattern = #{actionPattern}, actionname = #{actionName}, " +
            "equipment = #{equipment}, completed = #{completed}, actual_sets = #{actualSets}, " +
            "actual_reps = #{actualReps}, difficulty_score = #{difficultyScore}, note = #{note}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE username = #{username} AND plan_date = #{planDate} AND daytime = #{daytime} " +
            "AND action_index = #{actionIndex}")
    Integer updateByKey(PlanTaskRecord record);

    @Insert("INSERT INTO plantaskrecords(username, plan_date, daytime, action_index, actionpattern, actionname, equipment, " +
            "completed, actual_sets, actual_reps, difficulty_score, note, updated_at) " +
            "VALUES(#{username}, #{planDate}, #{daytime}, #{actionIndex}, #{actionPattern}, #{actionName}, #{equipment}, " +
            "#{completed}, #{actualSets}, #{actualReps}, #{difficultyScore}, #{note}, CURRENT_TIMESTAMP)")
    void insert(PlanTaskRecord record);

    @Select("SELECT COUNT(*) FROM plantaskrecords WHERE username = #{username} AND plan_date >= #{startDate} AND plan_date < #{endDate}")
    Integer countInRange(@Param("username") String username,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate);

    @Select("SELECT COALESCE(SUM(CASE WHEN completed = TRUE THEN 1 ELSE 0 END), 0) FROM plantaskrecords " +
            "WHERE username = #{username} AND plan_date >= #{startDate} AND plan_date < #{endDate}")
    Integer countCompletedInRange(@Param("username") String username,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
}
