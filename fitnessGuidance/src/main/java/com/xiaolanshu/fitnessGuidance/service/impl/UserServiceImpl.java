package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.UserMapper;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper usermapper;

    @Override
    public User findUsername(String username) {
        User preu =usermapper.findUsername(username);
        return preu;
    }

    @Override
    public void register(String username, String password,String identity) {
        usermapper.insert(username, PasswordUtil.hash(password),identity);
    }

    @Override
    public void adduser(String username,String password,String identity)
    {
        usermapper.adduser(username, PasswordUtil.hash(password),identity);
    }

    @Override
    public void editregister(String username,Boolean registered)
    {
        usermapper.editregister(username,registered);
    }

    @Override
    public ArrayList<User> getallmessage()
    {
        return usermapper.getallmessage();
    }

    @Override
    public void deleteuser(String username)
    {
        usermapper.deleteuser(username);
    }

    @Override
    public void editallmessage(String username,String nickname,String password,String userpic,
                               String identity,String specialty,Double height,Double weight){
        User existing = usermapper.findUsername(username);
        String nextPassword = existing != null ? existing.getPassword() : null;
        if (password != null && !password.isBlank()) {
            nextPassword = PasswordUtil.hash(password);
        }
        usermapper.updateall(username,nickname,nextPassword,userpic,identity,specialty,height,weight);
    }
}
