package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.NutritionPreferenceMapper;
import com.xiaolanshu.fitnessGuidance.pojo.NutritionPreference;
import com.xiaolanshu.fitnessGuidance.service.NutritionPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NutritionPreferenceServiceImpl implements NutritionPreferenceService {

    @Autowired
    private NutritionPreferenceMapper nutritionPreferenceMapper;

    @Override
    public NutritionPreference getPreference(String username) {
        NutritionPreference preference = nutritionPreferenceMapper.findByUsername(username);
        if (preference != null) {
            return preference;
        }
        NutritionPreference fallback = new NutritionPreference();
        fallback.setUsername(username);
        fallback.setDietType("均衡饮食");
        fallback.setAllergies("");
        fallback.setBudgetLevel("中等");
        fallback.setEatingOutFrequency("偶尔外食");
        fallback.setMealCount(4);
        fallback.setTastePreference("清淡");
        return fallback;
    }

    @Override
    public void savePreference(String username, NutritionPreference preference) {
        preference.setUsername(username);
        normalize(preference);
        Integer updated = nutritionPreferenceMapper.update(preference);
        if (updated == null || updated == 0) {
            nutritionPreferenceMapper.insert(preference);
        }
    }

    private void normalize(NutritionPreference preference) {
        if (preference.getDietType() == null || preference.getDietType().isBlank()) {
            preference.setDietType("均衡饮食");
        }
        if (preference.getBudgetLevel() == null || preference.getBudgetLevel().isBlank()) {
            preference.setBudgetLevel("中等");
        }
        if (preference.getEatingOutFrequency() == null || preference.getEatingOutFrequency().isBlank()) {
            preference.setEatingOutFrequency("偶尔外食");
        }
        if (preference.getMealCount() == null || preference.getMealCount() < 3) {
            preference.setMealCount(4);
        }
        if (preference.getMealCount() > 5) {
            preference.setMealCount(5);
        }
        if (preference.getAllergies() == null) {
            preference.setAllergies("");
        }
        if (preference.getTastePreference() == null || preference.getTastePreference().isBlank()) {
            preference.setTastePreference("清淡");
        }
    }
}
