package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.FitnessPlanRule;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.service.FitnessPlanRuleService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fitnessplanrule")
public class FitnessPlanRuleController {

    @Autowired
    private FitnessPlanRuleService fitnessPlanRuleService;

    @GetMapping("/getfitnessplanrule")
    public Result<FitnessPlanRule> getfitnessplanrule(String jwttoken, String fitnessGoal, Integer weeklyFrequency)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        FitnessPlanRule fitnessPlanRule = fitnessPlanRuleService.getfitnessplanrule(fitnessGoal,weeklyFrequency);
        return Result.success(fitnessPlanRule);
    }

    public FitnessPlanRule getfitnessplanrule( String fitnessGoal, Integer weeklyFrequency)
    {
        FitnessPlanRule fitnessPlanRule = fitnessPlanRuleService.getfitnessplanrule(fitnessGoal,weeklyFrequency);
        return fitnessPlanRule;
    }
}
