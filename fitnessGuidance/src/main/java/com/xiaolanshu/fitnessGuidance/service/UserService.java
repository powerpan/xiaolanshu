package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.User;

import java.util.ArrayList;

public interface UserService {
    User findUsername(String username);

    void register(String username, String password,String identity);

    void adduser(String username, String password,String identity);

    void editregister(String username,Boolean registered);

    ArrayList<User> getallmessage();

    void deleteuser(String username);

    void editallmessage(String username,String nickname,String password,String userpic,
                        String identity,String specialty,Double height,Double weight);
}
