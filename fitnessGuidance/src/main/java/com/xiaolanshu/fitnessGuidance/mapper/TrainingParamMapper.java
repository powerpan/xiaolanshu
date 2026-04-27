package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.TrainingParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TrainingParamMapper {

    @Select("Select * from trainingparams WHERE actionPattern=#{actionPattern} AND exLevel=#{exLevel}")
    TrainingParam gettrainingparam(String actionPattern, String exLevel);

}
