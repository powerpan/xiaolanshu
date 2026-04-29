package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.service.ExerciseGuideService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/exerciseguide")
public class ExerciseGuideController {

    @Autowired
    private ExerciseGuideService exerciseGuideService;

    @GetMapping("/getexerciseguide")
    public Result<ExerciseGuide> getexerciseguide(@RequestHeader(name = "Authorization", required = false) String authorization,
                                                  String jwttoken, String actionPattern, String equipment)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        ExerciseGuide exerciseGuide = exerciseGuideService.getexerciseguide(actionPattern,equipment);
        //System.out.println(exerciseGuide);
        return Result.success(exerciseGuide);
    }

    @GetMapping("/list")
    public Result<ArrayList<ExerciseGuide>> list(@RequestHeader(name = "Authorization", required = false) String authorization,
                                                 String jwttoken, String actionPattern, String equipment) {
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            return Result.error("未登录");
        }
        return Result.success(exerciseGuideService.listexerciseguides(actionPattern, equipment));
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

}
