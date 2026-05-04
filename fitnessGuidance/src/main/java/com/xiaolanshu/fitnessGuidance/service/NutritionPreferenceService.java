package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.NutritionPreference;

public interface NutritionPreferenceService {
    NutritionPreference getPreference(String username);

    void savePreference(String username, NutritionPreference preference);
}
