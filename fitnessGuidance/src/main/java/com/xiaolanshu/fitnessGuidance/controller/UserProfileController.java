package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.pojo.UserProfile;
import com.xiaolanshu.fitnessGuidance.service.UserProfileService;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/userprofile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserService userService;

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
    public Result edituserprofile(@RequestHeader(name = "Authorization", required = false) String authorization,
                                  String username,String jwttoken,String fitnessGoal,Integer weeklyFrequency,
                                  String equipment,String exLevel)
    {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        String tokenUsername = String.valueOf(claims.get("username"));
        String targetUsername = username == null || username.isBlank() ? tokenUsername : username;
        if (!canManageProfile(tokenUsername, targetUsername)) {
            return Result.error("无相关权限");
        }
        userProfileService.edituserprofile(targetUsername,fitnessGoal,weeklyFrequency,equipment,exLevel);
        return Result.success();
    }

    @PutMapping("/adduserprofile")
    public Result adduserprofile(@RequestHeader(name = "Authorization", required = false) String authorization,
                                 String jwttoken, String username)
    {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        if (!isAdmin(String.valueOf(claims.get("username")))) {
            return Result.error("无相关权限");
        }
        userProfileService.adduserprofile(username,"保持健康",3,"徒手","新手");
        return Result.success();
    }

    @PutMapping("/deleteuserprofile")
    public Result deleteuserprofile(@RequestHeader(name = "Authorization", required = false) String authorization,
                                    String jwttoken, String username)
    {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        if (!isAdmin(String.valueOf(claims.get("username")))) {
            return Result.error("无相关权限");
        }
        userProfileService.deleteuserprofile(username);
        return Result.success();
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

    private Map<String, Object> parseClaims(String token) {
        try {
            return Jwtutil.parseToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean canManageProfile(String tokenUsername, String targetUsername) {
        return Objects.equals(tokenUsername, targetUsername) || isAdmin(tokenUsername);
    }

    private boolean isAdmin(String username) {
        User user = userService.findUsername(username);
        return user != null && Objects.equals(user.getIdentity(), "ADMIN");
    }
}
