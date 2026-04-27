package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.UserProfile;

public interface UserProfileService {
    UserProfile getuserprofile(String username);

    void edituserprofile(String username,String fitnessGoal,Integer weeklyFrequency, String equipment,String exLevel);

    void adduserprofile(String username,String fitnessGoal,Integer weeklyFrequency,String equipment,String exLevel);

    void deleteuserprofile(String username);
}
