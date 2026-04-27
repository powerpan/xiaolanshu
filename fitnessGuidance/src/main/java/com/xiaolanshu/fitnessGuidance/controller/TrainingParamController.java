package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.TrainingParam;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.TrainingParamService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/trainingparam")
public class TrainingParamController {

    @Autowired
    private TrainingParamService trainingParamService;

    @GetMapping("/gettrainingparam")
    public Result<TrainingParam> gettrainingparam(String jwttoken,String actionPattern,String exLevel)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        TrainingParam trainingParam = trainingParamService.gettrainingparam(actionPattern,exLevel);
        return Result.success(trainingParam);
    }
}
