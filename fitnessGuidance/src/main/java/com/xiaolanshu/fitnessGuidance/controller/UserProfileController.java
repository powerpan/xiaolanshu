package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.UserProfile;
import com.xiaolanshu.fitnessGuidance.service.UserProfileService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/userprofile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/getuserprofile")
    public Result<UserProfile> getuserprofile(@RequestHeader(name = "Authorization") String jwttoken)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        UserProfile userProfile = userProfileService.getuserprofile(String.valueOf(map.get("username")));
        return Result.success(userProfile);
    }

    @PutMapping("/edituserprofile")
    public Result edituserprofile(String username,String jwttoken,String fitnessGoal,Integer weeklyFrequency,
                                  String equipment,String exLevel)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        userProfileService.edituserprofile(username,fitnessGoal,weeklyFrequency,equipment,exLevel);
        return Result.success();
    }

    @PutMapping("/adduserprofile")
    public Result adduserprofile(String username)
    {
        userProfileService.adduserprofile(username,"保持健康",3,"徒手","新手");
        return Result.success();
    }

    @PutMapping("/deleteuserprofile")
    public Result deleteuserprofile(String username)
    {
        userProfileService.deleteuserprofile(username);
        return Result.success();
    }
}
