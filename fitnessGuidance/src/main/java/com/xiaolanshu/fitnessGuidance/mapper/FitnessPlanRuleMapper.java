package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.FitnessPlanRule;
import com.xiaolanshu.fitnessGuidance.utils.JsonTypeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FitnessPlanRuleMapper {

    @Select("SELECT * FROM fitnessplanrules WHERE fitnessGoal=#{fitnessGoal} AND weeklyFrequency=#{weeklyFrequency}")
    @Results(id = "fitnessPlanRuleMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "fitnessGoal", column = "fitnessgoal"),
            @Result(property = "weeklyFrequency", column = "weeklyfrequency"),
            @Result(property = "splitMode", column = "splitmode", typeHandler = JsonTypeHandler.class)
    })
    FitnessPlanRule getfitnessplanrule(String fitnessGoal, Integer weeklyFrequency);
}
