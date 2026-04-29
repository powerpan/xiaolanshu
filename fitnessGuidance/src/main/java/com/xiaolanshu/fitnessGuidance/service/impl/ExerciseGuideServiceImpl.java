package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.ExerciseGuideMapper;
import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import com.xiaolanshu.fitnessGuidance.service.ExerciseGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ExerciseGuideServiceImpl implements ExerciseGuideService {

    @Autowired
    private ExerciseGuideMapper exerciseGuideMapper;

    @Override
    public ExerciseGuide getexerciseguide(String actionPattern, String equipment){
        return exerciseGuideMapper.getexerciseguide(actionPattern,equipment);
    }

    @Override
    public ArrayList<ExerciseGuide> listexerciseguides(String actionPattern, String equipment) {
        String safePattern = actionPattern == null || actionPattern.isBlank() ? null : actionPattern;
        String safeEquipment = equipment == null || equipment.isBlank() ? null : equipment;
        return exerciseGuideMapper.listexerciseguides(safePattern, safeEquipment);
    }
}
