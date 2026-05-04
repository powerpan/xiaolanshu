package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseAlternative;
import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface ExerciseAlternativeMapper {

    @Select("SELECT id, source_guide_id AS \"sourceGuideId\", target_guide_id AS \"targetGuideId\", reason, priority " +
            "FROM exercisealternatives ORDER BY priority ASC, id ASC")
    ArrayList<ExerciseAlternative> list();

    @Select("SELECT g.id, g.actionpattern AS \"actionPattern\", g.actionname AS \"actionName\", g.equipment, " +
            "g.description, g.steps, g.tips, g.imageurl, g.imagecredit AS \"imageCredit\", g.imagesourceurl AS \"imageSourceUrl\", " +
            "g.primarymuscles AS \"primaryMuscles\", g.secondarymuscles AS \"secondaryMuscles\", g.difficulty, " +
            "g.contraindications, g.commonmistakes AS \"commonMistakes\", g.suitablefor AS \"suitableFor\", g.alternatives " +
            "FROM exerciseguides g " +
            "LEFT JOIN exercisealternatives ea ON ea.target_guide_id = g.id " +
            "LEFT JOIN exerciseguides source ON source.id = ea.source_guide_id " +
            "WHERE (source.actionpattern = #{actionPattern} OR g.actionpattern = #{actionPattern}) " +
            "AND (#{currentGuideId} IS NULL OR g.id != #{currentGuideId}) " +
            "ORDER BY CASE WHEN ea.priority IS NULL THEN 20 ELSE ea.priority END, " +
            "CASE WHEN g.equipment = #{preferredEquipment} THEN 0 ELSE 1 END, g.equipment, g.actionname LIMIT #{limit}")
    ArrayList<ExerciseGuide> alternativesForPattern(@Param("actionPattern") String actionPattern,
                                                    @Param("preferredEquipment") String preferredEquipment,
                                                    @Param("currentGuideId") Integer currentGuideId,
                                                    @Param("limit") Integer limit);

    @Insert("INSERT INTO exercisealternatives(source_guide_id, target_guide_id, reason, priority) " +
            "VALUES(#{sourceGuideId}, #{targetGuideId}, #{reason}, #{priority})")
    void insert(ExerciseAlternative alternative);

    @Delete("DELETE FROM exercisealternatives WHERE id = #{id}")
    void delete(Integer id);
}
