package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.ArticleService;
import com.xiaolanshu.fitnessGuidance.service.UserProfileService;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ArticleService articleService;

    @PostMapping("/register")
    public Result register(String username , String password , String identity)
    {
        if(username==null || password==null)
        {
            return Result.error("用户名和密码都不能为空");
        }
        else if(username.length()>50||password.length()>50)
        {
            return Result.error("用户名或密码长度过长");
        }
        else {
            User preu = userService.findUsername(username);
            if (preu == null) {
                userService.register(username, password,identity);
                return Result.success();
            } else {
                return Result.error("用户名被占用");
            }
        }
    }

    @PostMapping("/login")
    public Result<String> login(String username,String password)
    {
        if(username==null || password==null)
        {
            return Result.error("用户名和密码都不能为空");
        }
        else if(username.length()>50||password.length()>50)
        {
            return Result.error("用户名或密码长度过长");
        }
        else
        {
            User preu = userService.findUsername(username);
            if(preu==null){
                return Result.error("用户名不存在");
            }
            else {
                if (password.equals(preu.getPassword())) {
                    if(preu.getRegistered()==false)
                    {
                        return Result.error("该用户的注册未审核通过");
                    }
                    Map<String,Object> claims = new HashMap<>();
                    claims.put("id",preu.getId());
                    claims.put("username",preu.getUsername());
                    String jwttoken = Jwtutil.genToken(claims);
                    return Result.success(jwttoken);//回复一个令牌
                }
                else
                {
                    return Result.error("用户名或密码错误");
                }
            }
        }
    }

    @PutMapping("/editmessage")
    public Result editmessage(String username,String jwttoken,String nickname,String password,String userpic,String identity
            ,String specialty,Double height,Double weight)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            return Result.error("未登录");
        }
        userService.editallmessage(username,nickname,password,userpic,identity,specialty,height,weight);
        return Result.success();
    }

    @GetMapping("/getmessage")
    public Result<User> getmessage(@RequestHeader(name = "Authorization") String jwttoken)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        User preu = userService.findUsername(String.valueOf(map.get("username")));
        return Result.success(preu);
    }

    @PutMapping("/editallmessage")
    public Result editallmessage(String username,String jwttoken,String nickname,String password,String userpic,String identity
            ,String specialty,Double height,Double weight)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        User preu = userService.findUsername(String.valueOf(map.get("username")));
        if(!Objects.equals(preu.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        userService.editallmessage(username,nickname,password,userpic,identity,specialty,height,weight);
        return Result.success();
    }

    //管理员的特殊权限
    @GetMapping("/getallmessage")
    public Result<ArrayList<User>> getallmessage(@RequestHeader(name = "Authorization") String jwttoken)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        String myusername = String.valueOf(map.get("username"));
        User preu = userService.findUsername(myusername);
        ArrayList<User> alluser = userService.getallmessage();
        return Result.success(alluser);
    }

    //管理员的特殊权限
    @PutMapping("/editregister")
    public Result aditregister(String jwttoken,String username,Boolean registered)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        String myusername = String.valueOf(map.get("username"));
        User preu = userService.findUsername(myusername);
        if(!Objects.equals(preu.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        userService.editregister(username,registered);
        if(registered){
            userProfileService.adduserprofile(username,"保持健康",3,"徒手","新手");
        }else{
            userProfileService.deleteuserprofile(username);
        }
        return Result.success();
    }

    //管理员的特殊权限
    @PostMapping("/adduser")
    public Result adduser(String jwttoken,String username,String password,String identity)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        String myusername = String.valueOf(map.get("username"));
        User thepreu = userService.findUsername(myusername);
        if(!Objects.equals(thepreu.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        if(username.isEmpty() || password.isEmpty())
        {
            return Result.error("用户名和密码都不能为空");
        }
        else if(username.length()>50||password.length()>50)
        {
            return Result.error("用户名或密码长度过长");
        }
        else {
            User preu = userService.findUsername(username);
            if (preu == null) {
                userService.adduser(username, password,identity);
                userProfileService.adduserprofile(username,"保持健康",3,"徒手","新手");
                return Result.success();
            } else {
                return Result.error("用户名被占用");
            }
        }
    }

    //管理员的特殊权限
    @PutMapping ("/deleteuser")
    public Result deleteuser(String jwttoken,String username)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map = Jwtutil.parseToken(jwttoken);
        String myusername = String.valueOf(map.get("username"));
        User preu = userService.findUsername(myusername);
        if(!Objects.equals(preu.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        userService.deleteuser(username);
        userProfileService.deleteuserprofile(username);
        articleService.deleteallarticle(username);
        //System.out.println(username);
        return Result.success();
    }

}
