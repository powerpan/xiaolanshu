package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;

@Mapper
public interface ExerciseGuideMapper {

    @Select("SELECT id, actionpattern AS \"actionPattern\", actionname AS \"actionName\", " +
            "equipment, description, steps, tips, imageurl, imagecredit AS \"imageCredit\", " +
            "imagesourceurl AS \"imageSourceUrl\", primarymuscles AS \"primaryMuscles\", " +
            "secondarymuscles AS \"secondaryMuscles\", difficulty, contraindications, " +
            "commonmistakes AS \"commonMistakes\", suitablefor AS \"suitableFor\", alternatives FROM exerciseguides " +
            "WHERE actionpattern = #{actionPattern} " +
            "AND (equipment = #{equipment} OR equipment = '徒手') " +
            "ORDER BY CASE WHEN equipment = #{equipment} THEN 0 ELSE 1 END " +
            "LIMIT 1")
    ExerciseGuide getexerciseguide(@Param("actionPattern") String actionPattern,
                                   @Param("equipment") String equipment);

    @Select("<script>" +
            "SELECT id, actionpattern AS \"actionPattern\", actionname AS \"actionName\", " +
            "equipment, description, steps, tips, imageurl, imagecredit AS \"imageCredit\", " +
            "imagesourceurl AS \"imageSourceUrl\", primarymuscles AS \"primaryMuscles\", " +
            "secondarymuscles AS \"secondaryMuscles\", difficulty, contraindications, " +
            "commonmistakes AS \"commonMistakes\", suitablefor AS \"suitableFor\", alternatives FROM exerciseguides " +
            "WHERE 1 = 1 " +
            "<if test='actionPattern != null'>AND actionpattern = #{actionPattern} </if>" +
            "<if test='equipment != null'>AND equipment = #{equipment} </if>" +
            "<if test='missingImageOnly != null and missingImageOnly'>AND (imageurl IS NULL OR imageurl = '') </if>" +
            "<if test='missingStepsOnly != null and missingStepsOnly'>AND (steps IS NULL OR steps = '') </if>" +
            "<if test='missingTipsOnly != null and missingTipsOnly'>AND (tips IS NULL OR tips = '') </if>" +
            "<if test='missingMistakesOnly != null and missingMistakesOnly'>AND (commonmistakes IS NULL OR commonmistakes = '') </if>" +
            "<if test='missingAlternativesOnly != null and missingAlternativesOnly'>AND (alternatives IS NULL OR alternatives = '') </if>" +
            "<if test='incompleteOnly != null and incompleteOnly'>AND ((imageurl IS NULL OR imageurl = '') OR (steps IS NULL OR steps = '') OR (tips IS NULL OR tips = '') OR (commonmistakes IS NULL OR commonmistakes = '') OR (primarymuscles IS NULL OR primarymuscles = '') OR (alternatives IS NULL OR alternatives = '')) </if>" +
            "ORDER BY actionpattern, equipment, actionname" +
            "</script>")
    ArrayList<ExerciseGuide> listexerciseguides(@Param("actionPattern") String actionPattern,
                                                @Param("equipment") String equipment,
                                                @Param("missingImageOnly") Boolean missingImageOnly,
                                                @Param("incompleteOnly") Boolean incompleteOnly,
                                                @Param("missingStepsOnly") Boolean missingStepsOnly,
                                                @Param("missingTipsOnly") Boolean missingTipsOnly,
                                                @Param("missingMistakesOnly") Boolean missingMistakesOnly,
                                                @Param("missingAlternativesOnly") Boolean missingAlternativesOnly);

    @Insert("INSERT INTO exerciseguides(actionpattern, actionname, equipment, description, steps, tips, imageurl, imagecredit, imagesourceurl, " +
            "primarymuscles, secondarymuscles, difficulty, contraindications, commonmistakes, suitablefor, alternatives) " +
            "VALUES(#{actionPattern}, #{actionName}, #{equipment}, #{description}, #{steps}, #{tips}, #{imageurl}, #{imageCredit}, #{imageSourceUrl}, " +
            "#{primaryMuscles}, #{secondaryMuscles}, #{difficulty}, #{contraindications}, #{commonMistakes}, #{suitableFor}, #{alternatives})")
    void addExerciseGuide(ExerciseGuide exerciseGuide);

    @Update("UPDATE exerciseguides SET " +
            "actionpattern = #{actionPattern}, " +
            "actionname = #{actionName}, " +
            "equipment = #{equipment}, " +
            "description = #{description}, " +
            "steps = #{steps}, " +
            "tips = #{tips}, " +
            "imageurl = #{imageurl}, " +
            "imagecredit = #{imageCredit}, " +
            "imagesourceurl = #{imageSourceUrl}, " +
            "primarymuscles = #{primaryMuscles}, " +
            "secondarymuscles = #{secondaryMuscles}, " +
            "difficulty = #{difficulty}, " +
            "contraindications = #{contraindications}, " +
            "commonmistakes = #{commonMistakes}, " +
            "suitablefor = #{suitableFor}, " +
            "alternatives = #{alternatives} " +
            "WHERE id = #{id}")
    void updateExerciseGuide(ExerciseGuide exerciseGuide);

    @Delete("DELETE FROM exerciseguides WHERE id = #{id}")
    void deleteExerciseGuide(Integer id);

}
