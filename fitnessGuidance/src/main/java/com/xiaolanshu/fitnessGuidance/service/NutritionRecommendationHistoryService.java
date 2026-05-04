package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendation;
import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendationHistory;

import java.util.ArrayList;

public interface NutritionRecommendationHistoryService {
    void save(String username, NutritionRecommendation recommendation);

    ArrayList<NutritionRecommendationHistory> recent(String username);
}
