package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.ExerciseGuideMapper;
import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import com.xiaolanshu.fitnessGuidance.service.ExerciseGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExerciseGuideServiceImpl implements ExerciseGuideService {

    @Autowired
    private ExerciseGuideMapper exerciseGuideMapper;

    @Override
    public ExerciseGuide getexerciseguide(String actionPattern, String equipment){
        return exerciseGuideMapper.getexerciseguide(actionPattern,equipment);
    }
}
