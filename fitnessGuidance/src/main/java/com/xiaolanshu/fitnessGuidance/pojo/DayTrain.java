package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class DayTrain {
    private Integer id;
    private String splitMode; // 分化模式
    private ArrayList<String> actionPattern;//动作模式组
}
