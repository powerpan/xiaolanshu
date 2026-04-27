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
}
