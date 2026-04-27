package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.UserProfileMapper;
import com.xiaolanshu.fitnessGuidance.pojo.UserProfile;
import com.xiaolanshu.fitnessGuidance.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public UserProfile getuserprofile(String username){
        return userProfileMapper.getuserprofile(username);
    }

    @Override
    public void edituserprofile(String username, String fitnessGoal, Integer weeklyFrequency, String equipment, String exLevel) {
        userProfileMapper.edituserprofile(username,fitnessGoal,weeklyFrequency,equipment,exLevel);
    }

    @Override
    public void adduserprofile(String username,String fitnessGoal,Integer weeklyFrequency,String equipment,String exLevel){
        userProfileMapper.adduserprofile(username,fitnessGoal,weeklyFrequency,equipment,exLevel);
    }

    @Override
    public void deleteuserprofile(String username){
        userProfileMapper.deleteuserprofile(username);
    }
}
