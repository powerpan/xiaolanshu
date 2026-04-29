package com.xiaolanshu.fitnessGuidance.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    private String nickname;
    @JsonIgnore
    private String password;
    private String userpic;//用户头像
    private LocalDateTime createtime;//用户创建时间
    private Boolean registered;
    private String identity; //身份
    private String specialty;
    private Double height;
    private Double weight;
}
