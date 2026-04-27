package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.ActionDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActionDetailMapper {

    @Select("SELECT * FROM actiondetails " +
            "WHERE actionpattern = #{actionPattern} " +
            "AND (equipment = #{equipment} OR equipment = '徒手') " +
            "ORDER BY CASE WHEN equipment = #{equipment} THEN 0 ELSE 1 END " +
            "LIMIT 1")
    ActionDetail getactiondetail(String actionPattern, String equipment);
}
