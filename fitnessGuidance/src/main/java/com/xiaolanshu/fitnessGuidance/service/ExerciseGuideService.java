package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;

import java.util.ArrayList;

public interface ExerciseGuideService {

    ExerciseGuide getexerciseguide(String actionPattern, String equipment);

    ArrayList<ExerciseGuide> listexerciseguides(String actionPattern, String equipment, Boolean missingImageOnly,
                                                Boolean incompleteOnly, Boolean missingStepsOnly,
                                                Boolean missingTipsOnly, Boolean missingMistakesOnly,
                                                Boolean missingAlternativesOnly);

    ArrayList<ExerciseGuide> listAlternativeGuides(String actionPattern, String preferredEquipment, Integer currentGuideId, Integer limit);

    void addExerciseGuide(ExerciseGuide exerciseGuide);

    void updateExerciseGuide(ExerciseGuide exerciseGuide);

    void deleteExerciseGuide(Integer id);

}
