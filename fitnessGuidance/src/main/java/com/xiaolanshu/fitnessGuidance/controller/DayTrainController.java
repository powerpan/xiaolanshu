package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.DayTrain;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.service.DayTrainService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/daytrain")
public class DayTrainController {

    @Autowired
    private DayTrainService dayTrainService;

    @GetMapping("/getdaytrain")
    public Result<DayTrain> getdaytrain(String jwttoken, String splitMode)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        DayTrain dayTrain = dayTrainService.getdaytrain(splitMode);
        return Result.success(dayTrain);
    }
}
