package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.ActionDetail;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.service.ActionDetailService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/actiondetail")
public class ActionDetailController {

    @Autowired
    private ActionDetailService actionDetailService;

    @GetMapping("/getactiondetail")
    public Result<ActionDetail> getactiondetail(String jwttoken, String actionPattern, String equipment)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        ActionDetail actionDetail = actionDetailService.getactiondetail(actionPattern,equipment);
        return Result.success(actionDetail);
    }
}
