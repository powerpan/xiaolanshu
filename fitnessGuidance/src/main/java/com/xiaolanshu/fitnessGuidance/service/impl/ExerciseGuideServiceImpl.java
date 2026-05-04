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
    public ArrayList<ExerciseGuide> listexerciseguides(String actionPattern, String equipment, Boolean missingImageOnly) {
        String safePattern = actionPattern == null || actionPattern.isBlank() ? null : actionPattern;
        String safeEquipment = equipment == null || equipment.isBlank() ? null : equipment;
        return exerciseGuideMapper.listexerciseguides(safePattern, safeEquipment, Boolean.TRUE.equals(missingImageOnly));
    }

    @Override
    public void addExerciseGuide(ExerciseGuide exerciseGuide) {
        normalize(exerciseGuide);
        exerciseGuideMapper.addExerciseGuide(exerciseGuide);
    }

    @Override
    public void updateExerciseGuide(ExerciseGuide exerciseGuide) {
        normalize(exerciseGuide);
        exerciseGuideMapper.updateExerciseGuide(exerciseGuide);
    }

    @Override
    public void deleteExerciseGuide(Integer id) {
        exerciseGuideMapper.deleteExerciseGuide(id);
    }

    private void normalize(ExerciseGuide exerciseGuide) {
        if (exerciseGuide.getImageurl() != null && exerciseGuide.getImageurl().isBlank()) {
            exerciseGuide.setImageurl(null);
        }
        if (exerciseGuide.getImageCredit() != null && exerciseGuide.getImageCredit().isBlank()) {
            exerciseGuide.setImageCredit(null);
        }
        if (exerciseGuide.getImageSourceUrl() != null && exerciseGuide.getImageSourceUrl().isBlank()) {
            exerciseGuide.setImageSourceUrl(null);
        }
    }
}
