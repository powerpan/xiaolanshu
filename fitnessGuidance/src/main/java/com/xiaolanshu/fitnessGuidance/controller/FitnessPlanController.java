package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.*;
import com.xiaolanshu.fitnessGuidance.service.*;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Autowired
    private PlanTaskRecordService planTaskRecordService;

    @Autowired
    private FitnessCheckinService fitnessCheckinService;

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
                task.setPrimaryMuscles(exerciseGuide.getPrimaryMuscles());
                task.setSecondaryMuscles(exerciseGuide.getSecondaryMuscles());
                task.setDifficulty(exerciseGuide.getDifficulty());
                task.setContraindications(exerciseGuide.getContraindications());
                task.setCommonMistakes(exerciseGuide.getCommonMistakes());
                task.setSuitableFor(exerciseGuide.getSuitableFor());
                task.setAlternatives(exerciseGuide.getAlternatives());
                if (task.getDescription() == null || task.getDescription().isBlank()) {
                    task.setDescription(exerciseGuide.getDescription());
                }
            }

            enrichTask(task, userProfile);

            actionTasks.add(task);
        }

        return Result.success(actionTasks);
    }

    @GetMapping("/taskrecords")
    public Result<ArrayList<PlanTaskRecord>> taskRecords(@RequestHeader(name = "Authorization", required = false) String authorization,
                                                         String jwttoken, Integer daytime) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        return Result.success(planTaskRecordService.listToday(username, daytime));
    }

    @PutMapping("/taskrecord")
    public Result saveTaskRecord(@RequestHeader(name = "Authorization", required = false) String authorization,
                                 String jwttoken, PlanTaskRecord record) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        planTaskRecordService.saveToday(username, record);
        return Result.success();
    }

    @GetMapping("/insight")
    public Result<TrainingPlanInsight> insight(@RequestHeader(name = "Authorization", required = false) String authorization,
                                               String jwttoken) {
        String username = parseUsername(resolveToken(authorization, jwttoken));
        if (username == null) {
            return Result.error("未登录");
        }
        UserProfile userProfile = userProfileService.getuserprofile(username);
        if (userProfile == null) {
            return Result.error("用户信息不存在");
        }
        CheckinStats stats = fitnessCheckinService.getStats(username);
        Integer recordedTasks = planTaskRecordService.countThisWeek(username);
        Integer completedTasks = planTaskRecordService.countCompletedThisWeek(username);
        int completionRate = recordedTasks == null || recordedTasks == 0
                ? 0
                : (int) Math.round(completedTasks * 100.0 / recordedTasks);

        TrainingPlanInsight insight = new TrainingPlanInsight();
        insight.setWeeklyTarget(userProfile.getWeeklyFrequency());
        insight.setRecordedTasks(recordedTasks == null ? 0 : recordedTasks);
        insight.setCompletedTasks(completedTasks == null ? 0 : completedTasks);
        insight.setCompletionRate(completionRate);
        insight.setCurrentStreak(stats == null ? 0 : stats.getCurrentStreak());
        insight.setRecommendation(resolvePlanRecommendation(userProfile, stats, completionRate));
        insight.setRecoveryHint(resolveRecoveryHint(stats, completionRate));
        insight.setNextAdjustment(resolveNextAdjustment(userProfile, completionRate));
        return Result.success(insight);
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

    private void enrichTask(ActionTask task, UserProfile userProfile) {
        String pattern = task.getActionPattern();
        String goal = userProfile.getFitnessGoal();
        String level = userProfile.getExLevel();
        String equipment = userProfile.getEquipment();
        task.setTargetArea(resolveTargetArea(pattern));
        task.setTrainingFocus(resolveTrainingFocus(goal, level));
        task.setAlternative(resolveAlternative(pattern, equipment));
        task.setIntensityLevel(resolveIntensityLevel(goal, level));
        task.setPlanReason(resolvePlanReason(pattern, goal, equipment));
        task.setProgressionAdvice(resolveProgressionAdvice(level));
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

    private String resolveIntensityLevel(String fitnessGoal, String exLevel) {
        if ("提升力量".equals(fitnessGoal) || "增肌".equals(fitnessGoal)) {
            return "中高强度";
        }
        if ("减脂".equals(fitnessGoal)) {
            return "中等强度，控制组间休息";
        }
        if ("新手".equals(exLevel)) {
            return "基础强度";
        }
        return "中等强度";
    }

    private String resolvePlanReason(String actionPattern, String fitnessGoal, String equipment) {
        return "根据目标「" + fitnessGoal + "」和可用器材「" + equipment + "」，安排「" + actionPattern
                + "」来覆盖当天核心动作模式。";
    }

    private String resolveProgressionAdvice(String exLevel) {
        return switch (exLevel) {
            case "新手" -> "先完成最低组数；动作稳定后再增加次数，不建议直接加重量。";
            case "进阶" -> "若最后一组仍能保留 2 次余力，下次可增加一组或轻微加重。";
            case "熟练" -> "用节奏、停顿或负重做渐进，每次只调整一个变量。";
            case "资深" -> "根据恢复状态安排强弱日，避免每次都堆到最高容量。";
            default -> "保持动作质量优先，再考虑增加容量。";
        };
    }

    private String resolvePlanRecommendation(UserProfile userProfile, CheckinStats stats, int completionRate) {
        int currentStreak = stats == null || stats.getCurrentStreak() == null ? 0 : stats.getCurrentStreak();
        if (completionRate == 0 && currentStreak == 0) {
            return "本周还没有形成有效训练记录，先完成一次计划内训练，不建议额外加量。";
        }
        if (completionRate < 50) {
            return "本周完成率偏低，优先把每个动作做到最低组数，减少临时加练。";
        }
        if (completionRate >= 85 && currentStreak >= 3) {
            return "完成率和连续性较好，下次可选择一个主动作增加一组或小幅加重。";
        }
        return "当前执行稳定，保持计划频率，训练后继续记录动作完成情况。";
    }

    private String resolveRecoveryHint(CheckinStats stats, int completionRate) {
        int averageMinutes = stats == null || stats.getAverageMinutes() == null ? 0 : stats.getAverageMinutes();
        if (averageMinutes >= 70 && completionRate >= 80) {
            return "最近训练时长偏高，建议至少保留 1 天恢复或灵活恢复训练。";
        }
        if (completionRate < 50) {
            return "不要用一次超长训练弥补缺勤，先恢复稳定节奏。";
        }
        return "保持睡眠、水分和训练后蛋白补充，下一次训练前观察关节状态。";
    }

    private String resolveNextAdjustment(UserProfile userProfile, int completionRate) {
        String goal = userProfile.getFitnessGoal();
        if (completionRate >= 85 && ("增肌".equals(goal) || "提升力量".equals(goal))) {
            return "下一次只给一个主动作加量，辅助动作保持原计划。";
        }
        if (completionRate >= 85) {
            return "下一次可略缩短组间休息，提高训练密度。";
        }
        if (completionRate < 50) {
            return "下一次降低到最低组数，先保证完整完成。";
        }
        return "下一次保持当前组数，重点观察动作质量。";
    }
}
