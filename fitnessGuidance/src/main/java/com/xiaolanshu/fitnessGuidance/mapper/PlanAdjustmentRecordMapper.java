package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.PlanAdjustmentRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface PlanAdjustmentRecordMapper {

    @Select("SELECT id, username, reason, adjustment_type AS \"adjustmentType\", summary, created_at AS \"createdAt\" " +
            "FROM planadjustmentrecords WHERE username = #{username} ORDER BY created_at DESC LIMIT #{limit}")
    ArrayList<PlanAdjustmentRecord> recent(@Param("username") String username, @Param("limit") Integer limit);

    @Insert("INSERT INTO planadjustmentrecords(username, reason, adjustment_type, summary, created_at) " +
            "VALUES(#{username}, #{reason}, #{adjustmentType}, #{summary}, CURRENT_TIMESTAMP)")
    void insert(PlanAdjustmentRecord record);
}
