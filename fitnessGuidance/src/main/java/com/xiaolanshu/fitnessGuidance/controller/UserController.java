package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.ArticleService;
import com.xiaolanshu.fitnessGuidance.service.UserProfileService;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import com.xiaolanshu.fitnessGuidance.utils.PasswordUtil;
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
        if(username==null || username.isBlank() || password==null || password.isBlank())
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
                userService.register(username, password,"user");
                return Result.success();
            } else {
                return Result.error("用户名被占用");
            }
        }
    }

    @PostMapping("/login")
    public Result<String> login(String username,String password)
    {
        if(username==null || username.isBlank() || password==null || password.isBlank())
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
                if (PasswordUtil.matches(password, preu.getPassword())) {
                    if(Boolean.FALSE.equals(preu.getRegistered()))
                    {
                        return Result.error("该用户的注册未审核通过");
                    }
                    if (!PasswordUtil.isHashed(preu.getPassword())) {
                        userService.editallmessage(
                                preu.getUsername(),
                                preu.getNickname(),
                                password,
                                preu.getUserpic(),
                                preu.getIdentity(),
                                preu.getSpecialty(),
                                preu.getHeight(),
                                preu.getWeight());
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
    public Result editmessage(@RequestHeader(name = "Authorization", required = false) String authorization,
                              String username,String jwttoken,String nickname,String password,String userpic,String identity
            ,String specialty,Double height,Double weight)
    {
        //令牌验证
        Map<String, Object> claims;
        try {
            claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            return Result.error("未登录");
        }
        String tokenUsername = String.valueOf(claims.get("username"));
        User current = userService.findUsername(tokenUsername);
        if (current == null) {
            return Result.error("用户不存在");
        }
        userService.editallmessage(tokenUsername,nickname,password,userpic,current.getIdentity(),specialty,height,weight);
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
    public Result editallmessage(@RequestHeader(name = "Authorization", required = false) String authorization,
                                 String username,String jwttoken,String nickname,String password,String userpic,String identity
            ,String specialty,Double height,Double weight)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
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
        if(!Objects.equals(preu.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        ArrayList<User> alluser = userService.getallmessage();
        return Result.success(alluser);
    }

    //管理员的特殊权限
    @PutMapping("/editregister")
    public Result aditregister(@RequestHeader(name = "Authorization", required = false) String authorization,
                               String jwttoken,String username,Boolean registered)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
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
    public Result adduser(@RequestHeader(name = "Authorization", required = false) String authorization,
                          String jwttoken,String username,String password,String identity)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        String myusername = String.valueOf(map.get("username"));
        User thepreu = userService.findUsername(myusername);
        if(!Objects.equals(thepreu.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        if(username == null || username.isBlank() || password == null || password.isBlank())
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
    public Result deleteuser(@RequestHeader(name = "Authorization", required = false) String authorization,
                             String jwttoken,String username)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
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

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

}
