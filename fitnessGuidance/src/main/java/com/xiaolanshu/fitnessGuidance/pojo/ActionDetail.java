package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class ActionDetail {
    private Integer id;
    private String actionPattern; //动作模式
    private String equipment; //器械限制
    private String description; //动作描述
}
