package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.CheckinStats;
import com.xiaolanshu.fitnessGuidance.pojo.FitnessCheckin;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.service.FitnessCheckinService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkin")
public class FitnessCheckinController {

    @Autowired
    private FitnessCheckinService fitnessCheckinService;

    @GetMapping("/today")
    public Result<FitnessCheckin> today(@RequestHeader(name = "Authorization", required = false) String authorization,
                                        String jwttoken) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        return Result.success(fitnessCheckinService.getToday(username));
    }

    @PutMapping("/today")
    public Result saveToday(@RequestHeader(name = "Authorization", required = false) String authorization,
                            String jwttoken, Integer durationMinutes, String mood, String note) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        fitnessCheckinService.saveToday(username, durationMinutes, mood, note);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<CheckinStats> stats(@RequestHeader(name = "Authorization", required = false) String authorization,
                                      String jwttoken) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        return Result.success(fitnessCheckinService.getStats(username));
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

    private String parseUsername(String token) {
        try {
            Map<String, Object> claims = Jwtutil.parseToken(token);
            return String.valueOf(claims.get("username"));
        } catch (Exception e) {
            return null;
        }
    }
}
