package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.NutritionPreference;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NutritionPreferenceMapper {

    @Select("SELECT id, username, diettype AS \"dietType\", allergies, budgetlevel AS \"budgetLevel\", " +
            "eatingoutfrequency AS \"eatingOutFrequency\", mealcount AS \"mealCount\", " +
            "tastepreference AS \"tastePreference\" FROM nutritionpreferences WHERE username = #{username}")
    NutritionPreference findByUsername(String username);

    @Insert("INSERT INTO nutritionpreferences(username, diettype, allergies, budgetlevel, eatingoutfrequency, mealcount, tastepreference) " +
            "VALUES(#{username}, #{dietType}, #{allergies}, #{budgetLevel}, #{eatingOutFrequency}, #{mealCount}, #{tastePreference})")
    void insert(NutritionPreference preference);

    @Update("UPDATE nutritionpreferences SET diettype = #{dietType}, allergies = #{allergies}, " +
            "budgetlevel = #{budgetLevel}, eatingoutfrequency = #{eatingOutFrequency}, " +
            "mealcount = #{mealCount}, tastepreference = #{tastePreference} WHERE username = #{username}")
    Integer update(NutritionPreference preference);
}
