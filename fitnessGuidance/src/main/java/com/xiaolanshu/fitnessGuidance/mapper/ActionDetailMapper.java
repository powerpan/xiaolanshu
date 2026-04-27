package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.ActionDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActionDetailMapper {

    @Select("Select * from actiondetails WHERE actionPattern=#{actionPattern} AND equipment=#{equipment}")
    ActionDetail getactiondetail(String actionPattern, String equipment);
}
