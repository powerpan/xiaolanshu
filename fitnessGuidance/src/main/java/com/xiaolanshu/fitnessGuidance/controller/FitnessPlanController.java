package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.*;
import com.xiaolanshu.fitnessGuidance.service.*;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public Result<String> getsplitMode(@RequestHeader(name = "Authorization", required = false) String authorization,
                                       String jwttoken,Integer daytime){
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
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
    public Result<ArrayList<ActionTask>> getactionTask(@RequestHeader(name = "Authorization", required = false) String authorization,
                                                       String jwttoken, Integer daytime){
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            return Result.error("未登录");
        }

        Map<String, Object> map = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
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
                task.setImageCredit(exerciseGuide.getImageCredit());
                task.setImageSourceUrl(exerciseGuide.getImageSourceUrl());
                if (task.getDescription() == null || task.getDescription().isBlank()) {
                    task.setDescription(exerciseGuide.getDescription());
                }
            }

            enrichTask(task, userProfile);

            actionTasks.add(task);
        }

        return Result.success(actionTasks);
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

    private void enrichTask(ActionTask task, UserProfile userProfile) {
        String pattern = task.getActionPattern();
        String goal = userProfile.getFitnessGoal();
        String level = userProfile.getExLevel();
        String equipment = userProfile.getEquipment();
        task.setTargetArea(resolveTargetArea(pattern));
        task.setTrainingFocus(resolveTrainingFocus(goal, level));
        task.setAlternative(resolveAlternative(pattern, equipment));
    }

    private String resolveTargetArea(String actionPattern) {
        return switch (actionPattern) {
            case "水平推" -> "胸部、肩前束、肱三头肌";
            case "垂直推" -> "肩部、肱三头肌、上背稳定";
            case "水平拉" -> "背部厚度、肩胛控制、肱二头肌";
            case "垂直拉" -> "背阔肌、上背、握力";
            case "下肢蹲" -> "股四头肌、臀部、核心稳定";
            case "髋铰链" -> "臀腿后侧、下背稳定";
            case "单腿训练" -> "单侧腿部、臀中肌、平衡";
            case "核心稳定" -> "腹部抗伸展、抗旋转、骨盆控制";
            case "手臂弯举" -> "肱二头肌、前臂控制";
            case "手臂伸展" -> "肱三头肌、肘部稳定";
            case "灵活恢复" -> "肩、胸椎、髋、踝活动度";
            default -> "全身基础能力";
        };
    }

    private String resolveTrainingFocus(String fitnessGoal, String exLevel) {
        String levelText = switch (exLevel) {
            case "新手" -> "先稳定轨迹和呼吸，不追求力竭";
            case "进阶" -> "保持动作质量，同时逐步增加训练容量";
            case "熟练" -> "关注节奏、顶峰控制和组间恢复";
            case "资深" -> "用可恢复的强度推进，不牺牲动作标准";
            default -> "以动作稳定和可持续执行为主";
        };
        String goalText = switch (fitnessGoal) {
            case "增肌" -> "每组保留 1 到 3 次余力，重点积累有效次数";
            case "减脂" -> "控制休息节奏，提高整体训练密度";
            case "塑形" -> "动作幅度完整，保持目标肌群张力";
            case "提升力量" -> "每组准备充分，优先保证发力路径";
            default -> "训练后仍应保持可恢复状态";
        };
        return levelText + "；" + goalText + "。";
    }

    private String resolveAlternative(String actionPattern, String equipment) {
        return switch (actionPattern) {
            case "水平推" -> "可替换为俯卧撑、哑铃卧推、弹力带胸推或坐姿推胸机。";
            case "垂直推" -> "可替换为 Pike 俯卧撑、哑铃肩推、杠铃推举或肩推机。";
            case "水平拉" -> "可替换为反向划船、单臂哑铃划船、弹力带划船或坐姿划船机。";
            case "垂直拉" -> "可替换为弹力带下拉、辅助引体向上或高位下拉。";
            case "下肢蹲" -> "可替换为徒手深蹲、高脚杯深蹲、杠铃深蹲或腿举机。";
            case "髋铰链" -> "可替换为早安式、哑铃罗马尼亚硬拉、弹力带拉穿或绳索拉穿。";
            case "单腿训练" -> "可替换为反向箭步蹲、分腿蹲、台阶上步或扶墙单腿蹲。";
            case "核心稳定" -> "可替换为平板支撑、死虫、鸟狗、Pallof Press 或悬垂举膝。";
            case "手臂弯举" -> "可替换为毛巾自阻力弯举、哑铃弯举、弹力带弯举或绳索弯举。";
            case "手臂伸展" -> "可替换为窄距俯卧撑、椅上臂屈伸、哑铃臂屈伸或绳索下压。";
            case "灵活恢复" -> "可替换为肩绕环、胸椎旋转、髋屈伸动态拉伸或弹力带轻阻力激活。";
            default -> "优先选择同动作模式、同难度且器材可用的替代动作。";
        };
    }
}
