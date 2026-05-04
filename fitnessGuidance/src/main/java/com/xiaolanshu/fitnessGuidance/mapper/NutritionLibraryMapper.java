package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.EatingScenario;
import com.xiaolanshu.fitnessGuidance.pojo.FoodItem;
import com.xiaolanshu.fitnessGuidance.pojo.FoodReplacement;
import com.xiaolanshu.fitnessGuidance.pojo.MealTemplate;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;

@Mapper
public interface NutritionLibraryMapper {

    @Select("<script>" +
            "SELECT id, name, category, calories_per100g AS \"caloriesPer100g\", protein_per100g AS \"proteinPer100g\", " +
            "carbohydrate_per100g AS \"carbohydratePer100g\", fat_per100g AS \"fatPer100g\", tags, scene FROM fooditems " +
            "WHERE 1 = 1 <if test='category != null'>AND category = #{category} </if> ORDER BY category, name" +
            "</script>")
    ArrayList<FoodItem> listFoodItems(@Param("category") String category);

    @Select("<script>" +
            "SELECT id, name, meal_type AS \"mealType\", goal, scene, target_calories AS \"targetCalories\", description, foods " +
            "FROM mealtemplates WHERE 1 = 1 " +
            "<if test='goal != null'>AND (goal = #{goal} OR goal = '通用') </if> " +
            "<if test='scene != null'>AND (scene = #{scene} OR scene = '通用') </if> " +
            "ORDER BY goal, meal_type, id" +
            "</script>")
    ArrayList<MealTemplate> listMealTemplates(@Param("goal") String goal, @Param("scene") String scene);

    @Select("SELECT id, source_food AS \"sourceFood\", replacement_food AS \"replacementFood\", reason, category " +
            "FROM foodreplacements ORDER BY category, id")
    ArrayList<FoodReplacement> listFoodReplacements();

    @Select("<script>" +
            "SELECT id, name, goal, strategy, avoid, example FROM eatingscenarios WHERE 1 = 1 " +
            "<if test='goal != null'>AND (goal = #{goal} OR goal = '通用') </if> ORDER BY name, goal" +
            "</script>")
    ArrayList<EatingScenario> listEatingScenarios(@Param("goal") String goal);

    @Insert("INSERT INTO fooditems(name, category, calories_per100g, protein_per100g, carbohydrate_per100g, fat_per100g, tags, scene) " +
            "VALUES(#{name}, #{category}, #{caloriesPer100g}, #{proteinPer100g}, #{carbohydratePer100g}, #{fatPer100g}, #{tags}, #{scene})")
    void insertFoodItem(FoodItem item);

    @Update("UPDATE fooditems SET name = #{name}, category = #{category}, calories_per100g = #{caloriesPer100g}, " +
            "protein_per100g = #{proteinPer100g}, carbohydrate_per100g = #{carbohydratePer100g}, fat_per100g = #{fatPer100g}, " +
            "tags = #{tags}, scene = #{scene} WHERE id = #{id}")
    void updateFoodItem(FoodItem item);

    @Delete("DELETE FROM fooditems WHERE id = #{id}")
    void deleteFoodItem(Integer id);

    @Insert("INSERT INTO mealtemplates(name, meal_type, goal, scene, target_calories, description, foods) " +
            "VALUES(#{name}, #{mealType}, #{goal}, #{scene}, #{targetCalories}, #{description}, #{foods})")
    void insertMealTemplate(MealTemplate item);

    @Update("UPDATE mealtemplates SET name = #{name}, meal_type = #{mealType}, goal = #{goal}, scene = #{scene}, " +
            "target_calories = #{targetCalories}, description = #{description}, foods = #{foods} WHERE id = #{id}")
    void updateMealTemplate(MealTemplate item);

    @Delete("DELETE FROM mealtemplates WHERE id = #{id}")
    void deleteMealTemplate(Integer id);

    @Insert("INSERT INTO foodreplacements(source_food, replacement_food, reason, category) " +
            "VALUES(#{sourceFood}, #{replacementFood}, #{reason}, #{category})")
    void insertFoodReplacement(FoodReplacement item);

    @Update("UPDATE foodreplacements SET source_food = #{sourceFood}, replacement_food = #{replacementFood}, " +
            "reason = #{reason}, category = #{category} WHERE id = #{id}")
    void updateFoodReplacement(FoodReplacement item);

    @Delete("DELETE FROM foodreplacements WHERE id = #{id}")
    void deleteFoodReplacement(Integer id);

    @Insert("INSERT INTO eatingscenarios(name, goal, strategy, avoid, example) " +
            "VALUES(#{name}, #{goal}, #{strategy}, #{avoid}, #{example})")
    void insertEatingScenario(EatingScenario item);

    @Update("UPDATE eatingscenarios SET name = #{name}, goal = #{goal}, strategy = #{strategy}, avoid = #{avoid}, example = #{example} " +
            "WHERE id = #{id}")
    void updateEatingScenario(EatingScenario item);

    @Delete("DELETE FROM eatingscenarios WHERE id = #{id}")
    void deleteEatingScenario(Integer id);
}
