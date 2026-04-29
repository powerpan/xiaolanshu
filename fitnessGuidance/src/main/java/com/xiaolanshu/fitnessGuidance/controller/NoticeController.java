package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Notice;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.NoticeService;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private UserService userService;

    @GetMapping("/getnotice")
    public Result<ArrayList<Notice>> getnotice(@RequestHeader(name = "Authorization", required = false) String authorization,
                                               String jwttoken)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        ArrayList<Notice> notices = noticeService.getnotice();
        return Result.success(notices);
    }

    @GetMapping("/getnoticedetail")
    public Result<Notice> getnoticedetail(@RequestHeader(name = "Authorization", required = false) String authorization,
                                          String jwttoken, Integer id)
    {
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            return Result.error("未登录");
        }
        Notice notice = noticeService.getNoticeDetail(id);
        if (notice == null) {
            return Result.error("公告不存在");
        }
        return Result.success(notice);
    }

    //管理员的特殊权限
    @PutMapping("/addnotice")
    public Result addnotice(@RequestHeader(name = "Authorization", required = false) String authorization,
                            String jwttoken,String title,String content)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        String authorname = String.valueOf(map.get("username"));
        User u =userService.findUsername(authorname);
        if(!Objects.equals(u.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        noticeService.addnotice(authorname,title,content);
        return Result.success();
    }

    @PutMapping("/deletenotice")
    public Result deletenotice(@RequestHeader(name = "Authorization", required = false) String authorization,
                               String jwttoken,String title,String content)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        String authorname = String.valueOf(map.get("username"));
        User u =userService.findUsername(authorname);
        if(!Objects.equals(u.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        noticeService.deletenotice(title,content);
        return Result.success();
    }

    @PutMapping("/editnotice")
    public Result editnotice(@RequestHeader(name = "Authorization", required = false) String authorization,
                             String jwttoken,String lasttitle,String lastcontent,String title,String content)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        String authorname = String.valueOf(map.get("username"));
        User u =userService.findUsername(authorname);
        if(!Objects.equals(u.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        noticeService.editnotice(lasttitle,lastcontent,title,content);
        return Result.success();
    }

    @PutMapping("/savetempnotice")
    public Result savetempnotice(@RequestHeader(name = "Authorization", required = false) String authorization,
                                 String jwttoken,String title,String content)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        String authorname = String.valueOf(map.get("username"));
        User u =userService.findUsername(authorname);
        if(!Objects.equals(u.getIdentity(), "ADMIN"))
        {
            return Result.error("无相关权限");
        }
        noticeService.savetempnotice(title,content,authorname);
        return Result.success();
    }

    @GetMapping("/gettempnotice")
    public Result gettempnotice(@RequestHeader(name = "Authorization") String jwttoken)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(jwttoken);
        String authorname = String.valueOf(map.get("username"));
        User u =userService.findUsername(authorname);
        if(!Objects.equals(u.getIdentity(), "ADMIN")) {
            return Result.error("无相关权限");
        }
        return Result.success(noticeService.gettempnotice(authorname));
    }

    @PutMapping("/deletetempnotice")
    public Result deletetempnotice(@RequestHeader(name = "Authorization", required = false) String authorization,
                                   String jwttoken)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        Map<String , Object> map =Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        String authorname = String.valueOf(map.get("username"));
        User u =userService.findUsername(authorname);
        if(!Objects.equals(u.getIdentity(), "ADMIN")) {
            return Result.error("无相关权限");
        }
        noticeService.deletetempnotice(authorname);
        return Result.success();
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }
}
