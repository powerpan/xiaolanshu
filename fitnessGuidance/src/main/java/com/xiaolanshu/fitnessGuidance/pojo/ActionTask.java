package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ActionTask {
    private String actionPattern;//动作模式
    private String actionName;//推荐具体动作
    private String equipment;//推荐器材
    private Integer minSets; //组数最小值
    private Integer maxSets; //组数最大值
    private Integer minReps; //次数最小值（个/组）
    private Integer maxReps; //次数最大值
    private Integer minRestSeconds; //组间最小间歇（分）
    private Integer maxRestSeconds; //组间最大间歇（分）
    private String description; //动作描述
    private String guideDescription; //动作指导说明
    private String steps; //执行步骤
    private String tips; //训练要点
    private String targetArea; //主要训练部位
    private String trainingFocus; //训练重点
    private String alternative; //替代动作建议
    private String planReason; //计划推荐理由
    private String intensityLevel; //今日强度提示
    private String progressionAdvice; //进阶或降级建议
    private String imageurl; //动作指导图
    private String imageCredit; //图片署名
    private String imageSourceUrl; //图片来源链接
    private String primaryMuscles; //主肌群
    private String secondaryMuscles; //辅助肌群
    private String difficulty; //动作难度
    private String contraindications; //禁忌和不适合情况
    private String commonMistakes; //常见错误
    private String suitableFor; //适合人群
    private String alternatives; //动作库替代动作
    private ArrayList<ExerciseGuide> alternativeGuides; //可点击替换动作
}
