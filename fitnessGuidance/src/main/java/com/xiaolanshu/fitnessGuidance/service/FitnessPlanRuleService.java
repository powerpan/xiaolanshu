package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.FitnessPlanRule;
import com.xiaolanshu.fitnessGuidance.pojo.Result;

public interface FitnessPlanRuleService {

    FitnessPlanRule getfitnessplanrule(String fitnessGoal, Integer weeklyFrequency);

}
