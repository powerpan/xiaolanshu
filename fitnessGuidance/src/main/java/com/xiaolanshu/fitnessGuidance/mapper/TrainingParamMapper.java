package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.TrainingParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TrainingParamMapper {

    @Select("SELECT * FROM trainingparams " +
            "WHERE actionpattern = #{actionPattern} " +
            "AND (exlevel = #{exLevel} OR exlevel = '新手') " +
            "ORDER BY CASE WHEN exlevel = #{exLevel} THEN 0 ELSE 1 END " +
            "LIMIT 1")
    TrainingParam gettrainingparam(String actionPattern, String exLevel);

}
