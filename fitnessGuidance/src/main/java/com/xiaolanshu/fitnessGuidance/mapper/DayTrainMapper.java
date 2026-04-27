package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.DayTrain;
import com.xiaolanshu.fitnessGuidance.utils.JsonTypeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DayTrainMapper {

    @Select("SELECT * FROM daytrains WHERE splitmode = #{splitmode}")
    @Results(id = "dayTrainMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "splitMode", column = "splitmode"),
            @Result(property = "actionPattern", column = "actionpattern", typeHandler = JsonTypeHandler.class)
    })
    DayTrain getdaytrain(String splitmode);
}
