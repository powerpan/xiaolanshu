package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.FitnessPlanRuleMapper;
import com.xiaolanshu.fitnessGuidance.pojo.FitnessPlanRule;
import com.xiaolanshu.fitnessGuidance.service.FitnessPlanRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FitnessPlanRuleServiceImpl implements FitnessPlanRuleService {

    @Autowired
    private FitnessPlanRuleMapper fitnessPlanRuleMapper;

    @Override
    public FitnessPlanRule getfitnessplanrule(String fitnessGoal, Integer weeklyFrequency){
        FitnessPlanRule rule = fitnessPlanRuleMapper.getfitnessplanrule(fitnessGoal,weeklyFrequency);
        if (rule != null) {
            return rule;
        }
        rule = fitnessPlanRuleMapper.getfitnessplanrule("保持健康",weeklyFrequency);
        if (rule != null) {
            return rule;
        }
        return fitnessPlanRuleMapper.getfitnessplanrule("保持健康",3);
    };

}
