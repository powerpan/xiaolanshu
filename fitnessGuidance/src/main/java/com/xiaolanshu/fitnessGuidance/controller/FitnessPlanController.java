package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.*;
import com.xiaolanshu.fitnessGuidance.service.*;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/fitnessplan")
public class FitnessPlanController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private FitnessPlanRuleService fitnessPlanRuleService;

    @Autowired
    private DayTrainService dayTrainService;

    @Autowired
    private TrainingParamService trainingParamService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private ExerciseGuideService exerciseGuideService;

    @GetMapping("/getsplitmode")
    public Result<String> getsplitMode(String jwttoken,Integer daytime){
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        String username = String.valueOf(map.get("username"));
        UserProfile userProfile = userProfileService.getuserprofile(username); // 拿到用户信息
        if (userProfile == null) {
            return Result.error("用户信息不存在");
        }
        FitnessPlanRule fitnessPlanRule = fitnessPlanRuleService.getfitnessplanrule(userProfile.getFitnessGoal(),
                userProfile.getWeeklyFrequency()); //拿到对应训练分化模式
        if (fitnessPlanRule == null || fitnessPlanRule.getSplitMode() == null || fitnessPlanRule.getSplitMode().isEmpty()) {
            return Result.error("未找到训练计划，请先完善健身需求");
        }
        ArrayList<String> splitModes = fitnessPlanRule.getSplitMode();
        if (daytime == null || daytime < 1 || daytime > splitModes.size()) {
            return Result.error("无效的训练天数参数");
        }
        return Result.success(splitModes.get(daytime-1));
    }

    @GetMapping("/getactiontask")
    public Result<ArrayList<ActionTask>> getactionTask(String jwttoken, Integer daytime){
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            return Result.error("未登录");
        }

        Map<String, Object> map = Jwtutil.parseToken(jwttoken);
        String username = String.valueOf(map.get("username"));

        UserProfile userProfile = userProfileService.getuserprofile(username);
        if (userProfile == null) {
            return Result.error("用户信息不存在");
        }

        FitnessPlanRule fitnessPlanRule = fitnessPlanRuleService.getfitnessplanrule(
                userProfile.getFitnessGoal(),
                userProfile.getWeeklyFrequency()
        );

        if (fitnessPlanRule == null || fitnessPlanRule.getSplitMode() == null) {
            return Result.error("未找到训练计划，请先完善健身需求");
        }

        ArrayList<String> splitModes = fitnessPlanRule.getSplitMode();

        if (daytime == null || daytime < 1 || daytime > splitModes.size()) {
            return Result.error("无效的训练天数参数");
        }

        // 获取当天的训练分化模式
        String daySplitMode = splitModes.get(daytime - 1);
        DayTrain dayTrain = dayTrainService.getdaytrain(daySplitMode);

        if (dayTrain == null || dayTrain.getActionPattern() == null) {
            return Result.error("未找到当天的训练动作");
        }

        // 获取当天的动作列表
        ArrayList<String> actionPatterns = dayTrain.getActionPattern();

        // 根据动作数量创建 ActionTask 列表，而不是根据 daytime
        ArrayList<ActionTask> actionTasks = new ArrayList<>();
        for (int i = 0; i < actionPatterns.size(); i++) {
            ActionTask task = new ActionTask();
            String actionPattern = actionPatterns.get(i);
            task.setActionPattern(actionPattern);

            // 拿到具体训练参数
            TrainingParam trainingParam = trainingParamService.gettrainingparam(
                    actionPattern,
                    userProfile.getExLevel()
            );

            if (trainingParam != null) {
                task.setMaxSets(trainingParam.getMaxSets());
                task.setMinSets(trainingParam.getMinSets());
                task.setMaxReps(trainingParam.getMaxReps());
                task.setMinReps(trainingParam.getMinReps());
                task.setMaxRestSeconds(trainingParam.getMaxRestSeconds());
                task.setMinRestSeconds(trainingParam.getMinRestSeconds());
            }

            // 拿到动作描述
            ActionDetail actionDetail = actionDetailService.getactiondetail(
                    actionPattern,
                    userProfile.getEquipment()
            );

            if (actionDetail != null) {
                task.setDescription(actionDetail.getDescription());
            }

            ExerciseGuide exerciseGuide = exerciseGuideService.getexerciseguide(
                    actionPattern,
                    userProfile.getEquipment()
            );

            if (exerciseGuide != null) {
                task.setActionName(exerciseGuide.getActionName());
                task.setEquipment(exerciseGuide.getEquipment());
                task.setGuideDescription(exerciseGuide.getDescription());
                task.setSteps(exerciseGuide.getSteps());
                task.setTips(exerciseGuide.getTips());
                task.setImageurl(exerciseGuide.getImageurl());
                if (task.getDescription() == null || task.getDescription().isBlank()) {
                    task.setDescription(exerciseGuide.getDescription());
                }
            }

            actionTasks.add(task);
        }

        return Result.success(actionTasks);
    }
}
