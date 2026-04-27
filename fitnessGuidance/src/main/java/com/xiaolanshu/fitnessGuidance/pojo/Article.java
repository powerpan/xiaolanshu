package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Article {
    private Integer id;
    private String title;
    private String content;
    private String author;
    private String topic;
    private LocalDateTime articletime;
}
