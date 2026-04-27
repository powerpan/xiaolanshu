package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendation;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.pojo.UserProfile;
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
        return Result.success(buildRecommendation(user, profile));
    }

    private NutritionRecommendation buildRecommendation(User user, UserProfile profile) {
        double weight = user != null && user.getWeight() != null ? user.getWeight() : 65D;
        String goal = profile != null && profile.getFitnessGoal() != null ? profile.getFitnessGoal() : "保持健康";

        int calories = (int) Math.round(weight * 30);
        double proteinRatio = 1.5;

        if ("增肌".equals(goal) || "提升力量".equals(goal)) {
            calories += 300;
            proteinRatio = 1.8;
        } else if ("减脂".equals(goal) || "塑形".equals(goal)) {
            calories -= 250;
            proteinRatio = 1.7;
        }

        NutritionRecommendation recommendation = new NutritionRecommendation();
        recommendation.setTargetCalories(Math.max(calories, 1300));
        recommendation.setProteinGrams((int) Math.round(weight * proteinRatio));
        recommendation.setWaterMl((int) Math.round(weight * 35));
        recommendation.setSummary("根据当前目标「" + goal + "」生成的日常饮食参考，可按训练日饥饿感上下微调。");

        ArrayList<String> meals = new ArrayList<>();
        meals.add("早餐：全谷物主食 + 鸡蛋或无糖酸奶 + 一份水果");
        meals.add("午餐：优质蛋白 + 米饭或薯类 + 两份蔬菜");
        meals.add("训练前后：香蕉、牛奶、酸奶或少量坚果，避免空腹硬练");
        meals.add("晚餐：清淡蛋白 + 大量蔬菜 + 适量主食，睡前避免高油高糖");
        recommendation.setMeals(meals);

        ArrayList<String> tips = new ArrayList<>();
        tips.add("每餐先保证蛋白质，再调整主食和脂肪。");
        tips.add("训练日可以略提高碳水，休息日保持蔬菜和水分。");
        tips.add("连续两周体重和围度无变化时，再微调热量。");
        recommendation.setTips(tips);

        return recommendation;
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
