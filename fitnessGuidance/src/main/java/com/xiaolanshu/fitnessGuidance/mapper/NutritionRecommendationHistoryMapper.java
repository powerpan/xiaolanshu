package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendationHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface NutritionRecommendationHistoryMapper {

    @Insert("INSERT INTO nutritionrecommendationhistories(username, targetcalories, proteingrams, carbohydrategrams, fatgrams, preferencesummary, summary, created_at) " +
            "VALUES(#{username}, #{targetCalories}, #{proteinGrams}, #{carbohydrateGrams}, #{fatGrams}, #{preferenceSummary}, #{summary}, CURRENT_TIMESTAMP)")
    void insert(NutritionRecommendationHistory history);

    @Select("SELECT id, username, targetcalories AS \"targetCalories\", proteingrams AS \"proteinGrams\", " +
            "carbohydrategrams AS \"carbohydrateGrams\", fatgrams AS \"fatGrams\", preferencesummary AS \"preferenceSummary\", " +
            "summary, created_at AS \"createdAt\" FROM nutritionrecommendationhistories " +
            "WHERE username = #{username} ORDER BY created_at DESC LIMIT #{limit}")
    ArrayList<NutritionRecommendationHistory> recent(@Param("username") String username, @Param("limit") Integer limit);
}
