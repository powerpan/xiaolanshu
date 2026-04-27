package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notice {
    private Integer id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime noticetime;
}
