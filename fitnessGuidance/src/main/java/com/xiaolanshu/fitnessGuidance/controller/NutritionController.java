package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendation;
import com.xiaolanshu.fitnessGuidance.pojo.NutritionPreference;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.pojo.UserProfile;
import com.xiaolanshu.fitnessGuidance.service.NutritionPreferenceService;
import com.xiaolanshu.fitnessGuidance.service.UserProfileService;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/nutrition")
public class NutritionController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private NutritionPreferenceService nutritionPreferenceService;

    @GetMapping("/recommendation")
    public Result<NutritionRecommendation> recommendation(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }

        User user = userService.findUsername(username);
        UserProfile profile = userProfileService.getuserprofile(username);
        NutritionPreference preference = nutritionPreferenceService.getPreference(username);
        return Result.success(buildRecommendation(user, profile, preference));
    }

    @GetMapping("/preference")
    public Result<NutritionPreference> preference(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        return Result.success(nutritionPreferenceService.getPreference(username));
    }

    @PutMapping("/preference")
    public Result savePreference(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken,
            NutritionPreference preference) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        nutritionPreferenceService.savePreference(username, preference);
        return Result.success();
    }

    private NutritionRecommendation buildRecommendation(User user, UserProfile profile, NutritionPreference preference) {
        double weight = user != null && user.getWeight() != null ? user.getWeight() : 65D;
        String goal = profile != null && profile.getFitnessGoal() != null ? profile.getFitnessGoal() : "保持健康";
        int weeklyFrequency = profile != null && profile.getWeeklyFrequency() != null ? profile.getWeeklyFrequency() : 3;
        int mealCount = preference != null && preference.getMealCount() != null ? preference.getMealCount() : 4;

        int calories = (int) Math.round(weight * (28 + Math.min(weeklyFrequency, 6) * 1.2));
        double proteinRatio = 1.5;
        double fatRatio = 0.8;

        if ("增肌".equals(goal) || "提升力量".equals(goal)) {
            calories += 300;
            proteinRatio = 1.8;
            fatRatio = 0.9;
        } else if ("减脂".equals(goal) || "塑形".equals(goal)) {
            calories -= 250;
            proteinRatio = 1.7;
            fatRatio = 0.7;
        }

        NutritionRecommendation recommendation = new NutritionRecommendation();
        int safeCalories = Math.max(calories, 1300);
        int proteinGrams = (int) Math.round(weight * proteinRatio);
        int fatGrams = (int) Math.round(weight * fatRatio);
        int carbohydrateGrams = Math.max(100, (safeCalories - proteinGrams * 4 - fatGrams * 9) / 4);
        recommendation.setGoal(goal);
        recommendation.setWeightKg(weight);
        recommendation.setWeeklyFrequency(weeklyFrequency);
        recommendation.setTargetCalories(safeCalories);
        recommendation.setProteinGrams(proteinGrams);
        recommendation.setCarbohydrateGrams(carbohydrateGrams);
        recommendation.setFatGrams(fatGrams);
        recommendation.setWaterMl((int) Math.round(weight * (weeklyFrequency >= 4 ? 38 : 35)));
        recommendation.setSummary(buildSummary(goal, safeCalories, proteinGrams, carbohydrateGrams, fatGrams));
        recommendation.setPreferenceSummary(buildPreferenceSummary(preference, mealCount));
        recommendation.setTrainingDayTip(buildTrainingDayTip(goal));
        recommendation.setRestDayTip(buildRestDayTip(goal));

        ArrayList<String> meals = new ArrayList<>();
        meals.add(goalMeal("早餐", goal, "全谷物主食 + 鸡蛋/牛奶/无糖酸奶 + 一份水果"));
        meals.add(goalMeal("午餐", goal, "瘦肉/鱼虾/豆制品 + 米饭/薯类 + 两份蔬菜"));
        meals.add(goalMeal("训练前后", goal, "香蕉/燕麦/酸奶 + 低脂蛋白，避免空腹硬练"));
        meals.add(goalMeal("晚餐", goal, "清淡蛋白 + 大量蔬菜 + 按目标保留适量主食"));
        if (mealCount >= 5) {
            meals.add(goalMeal("加餐", goal, "低脂酸奶/水果/坚果小份，避免高糖零食"));
        }
        recommendation.setMeals(meals);

        ArrayList<String> trainingDayMeals = new ArrayList<>();
        trainingDayMeals.add("早餐：主食 + 优质蛋白 + 水果，保证上午能量。");
        trainingDayMeals.add("训练前：按饥饿程度补 30 到 60g 碳水。");
        trainingDayMeals.add("训练后：优先补 25 到 40g 蛋白质和水分。");
        trainingDayMeals.add("晚餐：低油蛋白 + 蔬菜 + 适量主食，避免训练后暴食。");
        recommendation.setTrainingDayMeals(limitMeals(trainingDayMeals, mealCount));

        ArrayList<String> restDayMeals = new ArrayList<>();
        restDayMeals.add("早餐：蛋白质不减，主食按饥饿程度控制。");
        restDayMeals.add("午餐：保持肉/蛋/豆制品和两份蔬菜。");
        restDayMeals.add("下午：需要加餐时优先水果、酸奶或豆浆。");
        restDayMeals.add("晚餐：主食略低于训练日，控制油脂和夜宵。");
        recommendation.setRestDayMeals(limitMeals(restDayMeals, mealCount));

        ArrayList<String> tips = new ArrayList<>();
        tips.add("每餐先保证蛋白质，再调整主食和脂肪。");
        tips.add("训练日优先把碳水放在训练前后，休息日保持蔬菜和水分。");
        tips.add("连续两周体重和围度无变化时，再微调热量。");
        tips.add("睡眠不足或训练状态差时，不建议通过继续削减热量来加速目标。");
        recommendation.setTips(tips);

        ArrayList<String> mealTiming = new ArrayList<>();
        mealTiming.add("训练前 1 到 2 小时：安排 30 到 60g 易消化碳水，搭配少量蛋白。");
        mealTiming.add("训练后 2 小时内：补充 25 到 40g 蛋白质，并补足水分。");
        mealTiming.add("晚间训练：晚餐减少油脂，避免影响睡眠和胃肠负担。");
        recommendation.setMealTiming(mealTiming);

        ArrayList<String> foodChoices = new ArrayList<>();
        foodChoices.add("蛋白质：鸡蛋、鸡胸、鱼虾、牛肉、豆腐、希腊酸奶。");
        foodChoices.add("碳水：米饭、燕麦、土豆、红薯、全麦面包、水果。");
        foodChoices.add("脂肪：橄榄油、牛油果、坚果、深海鱼，控制总量。");
        foodChoices.add("蔬菜：深色叶菜、菌菇、番茄、十字花科蔬菜。");
        recommendation.setFoodChoices(foodChoices);

        ArrayList<String> replacements = new ArrayList<>();
        replacements.add("鸡胸肉可替换为鱼虾、瘦牛肉、鸡蛋、豆腐或无糖酸奶。");
        replacements.add("米饭可替换为燕麦、土豆、红薯、全麦面包或玉米。");
        replacements.add("牛奶不适应时可替换为无糖豆浆、酸奶或豆制品。");
        replacements.add(resolvePreferenceReplacement(preference));
        recommendation.setReplacements(replacements);

        ArrayList<String> shoppingList = new ArrayList<>();
        shoppingList.add("蛋白：鸡蛋、豆腐、鱼虾、鸡胸或瘦牛肉。");
        shoppingList.add("主食：米饭、燕麦、红薯、土豆或全麦面包。");
        shoppingList.add("蔬果：深色叶菜、番茄、菌菇、香蕉或当季水果。");
        shoppingList.add(resolveBudgetShopping(preference));
        recommendation.setShoppingList(shoppingList);

        ArrayList<String> watchouts = new ArrayList<>();
        if ("减脂".equals(goal) || "塑形".equals(goal)) {
            watchouts.add("不要把热量压得过低；训练表现明显下降时先恢复睡眠和饮水。");
            watchouts.add("少喝含糖饮料和高油外卖，优先控制隐形热量。");
        } else if ("增肌".equals(goal) || "提升力量".equals(goal)) {
            watchouts.add("体重每周快速上升时，优先减少高油零食而不是减少正餐蛋白。");
            watchouts.add("力量训练日不要长期低碳，否则会影响训练容量。");
        } else {
            watchouts.add("保持三餐规律，避免通过极端断食弥补偶发饮食失控。");
        }
        recommendation.setWatchouts(watchouts);

        return recommendation;
    }

    private String buildSummary(String goal, int calories, int protein, int carbs, int fat) {
        return "当前目标「" + goal + "」建议日摄入约 " + calories + " kcal，蛋白质 "
                + protein + "g、碳水 " + carbs + "g、脂肪 " + fat + "g；训练日和休息日可围绕碳水做小幅调整。";
    }

    private String buildTrainingDayTip(String goal) {
        if ("增肌".equals(goal) || "提升力量".equals(goal)) {
            return "训练日前后保留足量碳水，训练后补充蛋白质，不建议空腹完成高强度力量训练。";
        }
        if ("减脂".equals(goal) || "塑形".equals(goal)) {
            return "训练前安排少量易消化碳水，训练后补足蛋白和水分，避免用高油零食奖励自己。";
        }
        return "训练前 1 到 2 小时优先安排易消化碳水；训练后 2 小时内补足蛋白质和水分。";
    }

    private String buildRestDayTip(String goal) {
        if ("增肌".equals(goal) || "提升力量".equals(goal)) {
            return "休息日也要保证蛋白质，碳水可略低于训练日，但不要低到影响恢复。";
        }
        if ("减脂".equals(goal) || "塑形".equals(goal)) {
            return "休息日主食略减，蔬菜和蛋白不减，重点控制饮料、零食和夜宵。";
        }
        return "休息日保持三餐规律和足量饮水，帮助下一次训练恢复。";
    }

    private String goalMeal(String mealName, String goal, String base) {
        if ("增肌".equals(goal) || "提升力量".equals(goal)) {
            return mealName + "：" + base + "，主食不要省，保证训练储备。";
        }
        if ("减脂".equals(goal) || "塑形".equals(goal)) {
            return mealName + "：" + base + "，优先低油烹饪并控制酱料。";
        }
        return mealName + "：" + base + "。";
    }

    private ArrayList<String> limitMeals(ArrayList<String> meals, int mealCount) {
        int limit = Math.min(Math.max(mealCount, 3), meals.size());
        return new ArrayList<>(meals.subList(0, limit));
    }

    private String buildPreferenceSummary(NutritionPreference preference, int mealCount) {
        if (preference == null) {
            return "当前按均衡饮食、每日至少 " + mealCount + " 餐生成建议。";
        }
        String allergies = preference.getAllergies() == null || preference.getAllergies().isBlank()
                ? "无明确忌口"
                : preference.getAllergies();
        return "偏好「" + preference.getDietType() + "」，预算「" + preference.getBudgetLevel()
                + "」，外食频率「" + preference.getEatingOutFrequency() + "」，每日 " + mealCount
                + " 餐，忌口：" + allergies + "，口味：" + preference.getTastePreference() + "。";
    }

    private String resolvePreferenceReplacement(NutritionPreference preference) {
        if (preference == null || preference.getDietType() == null) {
            return "外食时优先选择清蒸、炖煮、少油快炒，少选油炸和浓酱。";
        }
        if (preference.getDietType().contains("素")) {
            return "素食优先用豆腐、豆浆、鹰嘴豆、鸡蛋和奶制品补足蛋白。";
        }
        if (preference.getDietType().contains("控糖")) {
            return "控糖时主食优先选择低加工谷物，饮料改无糖茶或水。";
        }
        if (preference.getEatingOutFrequency() != null && preference.getEatingOutFrequency().contains("经常")) {
            return "经常外食时选择盖饭减半酱汁、加一份蔬菜，饮料换水。";
        }
        return "按可获得食材替换同类蛋白、同类主食和同类蔬菜即可。";
    }

    private String resolveBudgetShopping(NutritionPreference preference) {
        if (preference != null && "经济".equals(preference.getBudgetLevel())) {
            return "经济组合：鸡蛋、豆腐、鸡腿去皮、冷冻蔬菜、土豆和燕麦。";
        }
        if (preference != null && "充足".equals(preference.getBudgetLevel())) {
            return "充足组合：鱼虾、牛肉、希腊酸奶、坚果、浆果和多种深色蔬菜。";
        }
        return "中等预算组合：鸡蛋、鸡胸、鱼、豆腐、酸奶、米饭和当季蔬菜。";
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

    private String parseUsername(String token) {
        try {
            Map<String, Object> claims = Jwtutil.parseToken(token);
            return String.valueOf(claims.get("username"));
        } catch (Exception e) {
            return null;
        }
    }
}
