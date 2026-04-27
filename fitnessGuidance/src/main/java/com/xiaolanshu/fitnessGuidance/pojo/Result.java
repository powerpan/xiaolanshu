package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
//结果
@Data
public class Result<T> {
    private Integer status;//成功为1，失败为0
    private String message;//提示信息
    private T data;//携带数据
    public static <E> Result <E> success(E data)
    {
        return new Result(1,"操作完成",data);
    }

    public static Result success()
    {
        return new Result(1,"操作完成",null);
    }

    public static Result error(String message)
    {
        return new Result (0,message,null);
    }
}
