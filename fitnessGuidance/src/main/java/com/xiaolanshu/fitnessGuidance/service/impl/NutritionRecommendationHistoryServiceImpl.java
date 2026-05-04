package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.NutritionRecommendationHistoryMapper;
import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendation;
import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendationHistory;
import com.xiaolanshu.fitnessGuidance.service.NutritionRecommendationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NutritionRecommendationHistoryServiceImpl implements NutritionRecommendationHistoryService {

    @Autowired
    private NutritionRecommendationHistoryMapper nutritionRecommendationHistoryMapper;

    @Override
    public void save(String username, NutritionRecommendation recommendation) {
        NutritionRecommendationHistory history = new NutritionRecommendationHistory();
        history.setUsername(username);
        history.setTargetCalories(recommendation.getTargetCalories());
        history.setProteinGrams(recommendation.getProteinGrams());
        history.setCarbohydrateGrams(recommendation.getCarbohydrateGrams());
        history.setFatGrams(recommendation.getFatGrams());
        history.setPreferenceSummary(recommendation.getPreferenceSummary());
        history.setSummary(recommendation.getSummary());
        nutritionRecommendationHistoryMapper.insert(history);
    }

    @Override
    public ArrayList<NutritionRecommendationHistory> recent(String username) {
        return nutritionRecommendationHistoryMapper.recent(username, 5);
    }
}
