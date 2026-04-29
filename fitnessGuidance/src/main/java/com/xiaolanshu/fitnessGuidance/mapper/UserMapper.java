package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Mapper
public interface UserMapper {

    @Select("select * from users where username =#{username}")
    User findUsername(String username) ;//查询是否存在

    @Insert("insert into users(username,password,nickname,createtime,identity)"+
            "values(#{username},#{password},#{username},now(),#{identity})")
    void insert(String username, String password,String identity) ;//添加新注册的用户

    @Insert("insert into users(username,password,nickname,createtime,registered,identity)"+
            "values(#{username},#{password},#{username},now(),TRUE,#{identity})")
    void adduser(String username, String password,String identity);

    @Update("UPDATE users " +
            "SET registered = #{registered} " +
            "WHERE username = #{username}")
    void editregister(String username,Boolean registered);

    @Select("Select * from users WHERE username!='admin'")
    ArrayList<User> getallmessage();

    @Delete("DELETE from users WHERE username=#{username}")
    void deleteuser(String username);

    @Update("UPDATE users " +
            "SET nickname = #{nickname}, " +
            "password = #{password}, " +
            "identity = #{identity}, "+
            "specialty = #{specialty}, "+
            "userpic = #{userpic}, " +
            "height = #{height},"+
            "weight = #{weight} "+
            "WHERE username = #{username}")
    void updateall(String username, String nickname, String password, String userpic,
                   String identity, String specialty, Double height, Double weight);
}
