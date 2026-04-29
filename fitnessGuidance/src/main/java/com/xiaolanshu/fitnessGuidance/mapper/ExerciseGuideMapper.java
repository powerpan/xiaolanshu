package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface ExerciseGuideMapper {

    @Select("SELECT id, actionpattern AS \"actionPattern\", actionname AS \"actionName\", " +
            "equipment, description, steps, tips, imageurl, imagecredit AS \"imageCredit\", " +
            "imagesourceurl AS \"imageSourceUrl\" FROM exerciseguides " +
            "WHERE actionpattern = #{actionPattern} " +
            "AND (equipment = #{equipment} OR equipment = '徒手') " +
            "ORDER BY CASE WHEN equipment = #{equipment} THEN 0 ELSE 1 END " +
            "LIMIT 1")
    ExerciseGuide getexerciseguide(String actionPattern, String equipment);

    @Select("<script>" +
            "SELECT id, actionpattern AS \"actionPattern\", actionname AS \"actionName\", " +
            "equipment, description, steps, tips, imageurl, imagecredit AS \"imageCredit\", " +
            "imagesourceurl AS \"imageSourceUrl\" FROM exerciseguides " +
            "WHERE 1 = 1 " +
            "<if test='actionPattern != null'>AND actionpattern = #{actionPattern} </if>" +
            "<if test='equipment != null'>AND equipment = #{equipment} </if>" +
            "ORDER BY actionpattern, equipment, actionname" +
            "</script>")
    ArrayList<ExerciseGuide> listexerciseguides(@Param("actionPattern") String actionPattern,
                                                @Param("equipment") String equipment);

}
