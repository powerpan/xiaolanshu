package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExerciseGuideMapper {

    @Select("SELECT * FROM exerciseguides WHERE actionPattern = #{actionPattern} AND equipment = #{equipment}")
    ExerciseGuide getexerciseguide(String actionPattern, String equipment);

}
