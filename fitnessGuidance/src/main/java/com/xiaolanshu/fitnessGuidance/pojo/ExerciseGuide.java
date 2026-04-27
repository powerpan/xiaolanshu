package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class ExerciseGuide {
    private Integer id;
    private String actionPattern;//动作模式
    private String equipment;
    private String description; //动作描述
    private String imageurl;//图片路径
}
