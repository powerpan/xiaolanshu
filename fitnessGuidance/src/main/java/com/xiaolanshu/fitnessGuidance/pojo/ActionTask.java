package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class ActionTask {
    private String actionPattern;//动作模式
    private Integer minSets; //组数最小值
    private Integer maxSets; //组数最大值
    private Integer minReps; //次数最小值（个/组）
    private Integer maxReps; //次数最大值
    private Integer minRestSeconds; //组间最小间歇（分）
    private Integer maxRestSeconds; //组间最大间歇（分）
    private String description; //动作描述
}
