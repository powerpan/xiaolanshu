package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.UserProfile;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserProfileMapper {

    @Select("Select * from userprofiles WHERE username=#{username}")
    UserProfile getuserprofile(String username);

    @Update("UPDATE userprofiles " +
            "SET fitnessGoal = #{fitnessGoal}, " +
            "weeklyFrequency = #{weeklyFrequency}, " +
            "equipment = #{equipment}, "+
            "exLevel = #{exLevel} "+
            "WHERE username = #{username}")
    void edituserprofile(String username,String fitnessGoal,Integer weeklyFrequency,String equipment,String exLevel);


    @Insert("insert into userprofiles(username,fitnessGoal,weeklyFrequency,equipment,exLevel)"+
            "values(#{username},#{fitnessGoal},#{weeklyFrequency},#{equipment},#{exLevel})")
    void adduserprofile(String username,String fitnessGoal,Integer weeklyFrequency,String equipment,String exLevel);

    @Delete("DELETE from userprofiles WHERE username=#{username}")
    void deleteuserprofile(String username);
}
