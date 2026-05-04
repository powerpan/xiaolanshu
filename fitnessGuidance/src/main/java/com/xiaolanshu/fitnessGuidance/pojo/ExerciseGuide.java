package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class ExerciseGuide {
    private Integer id;
    private String actionPattern;//动作模式
    private String actionName;//具体动作名称
    private String equipment;
    private String description; //动作描述
    private String steps; //执行步骤
    private String tips; //训练要点
    private String imageurl;//图片路径
    private String imageCredit;//图片署名
    private String imageSourceUrl;//图片来源链接
    private String primaryMuscles;//主肌群
    private String secondaryMuscles;//辅助肌群
    private String difficulty;//动作难度
    private String contraindications;//禁忌和不适合情况
    private String commonMistakes;//常见错误
    private String suitableFor;//适合人群
    private String alternatives;//替代动作
}
