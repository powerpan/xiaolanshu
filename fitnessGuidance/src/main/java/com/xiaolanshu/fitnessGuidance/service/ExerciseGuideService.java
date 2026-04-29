package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;

import java.util.ArrayList;

public interface ExerciseGuideService {

    ExerciseGuide getexerciseguide(String actionPattern, String equipment);

    ArrayList<ExerciseGuide> listexerciseguides(String actionPattern, String equipment);

}
