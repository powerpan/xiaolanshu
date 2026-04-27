package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class TrainingParam {
    private Integer id;
    private String actionPattern; //行动模式
    private String exLevel; //经验等级
    private Integer minSets; //组数最小值
    private Integer maxSets; //组数最大值
    private Integer minReps; //次数最小值（个/组）
    private Integer maxReps; //次数最大值
    private Integer minRestSeconds; //组间最小间歇（分）
    private Integer maxRestSeconds; //组间最大间歇（分）
}
