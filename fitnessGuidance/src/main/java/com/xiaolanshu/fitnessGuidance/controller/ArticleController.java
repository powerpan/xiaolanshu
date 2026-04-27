package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Article;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.service.ArticleService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    //搜索所有
    @GetMapping("/getarticle")
    public Result<ArrayList<Article>> getarticle(String jwttoken,String title,String topic)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        ArrayList<Article> articles = articleService.getarticle(title,topic);
        return Result.success(articles);
    }

    //搜索且不带主题
    @GetMapping("/getarticlewithouttopic")
    public Result<ArrayList<Article>> getarticlewithouttopic(String jwttoken,String title)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(jwttoken);
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        ArrayList<Article> articles = articleService.getarticlewithouttopic(title);
        return Result.success(articles);
    }

    //搜索自己的文章
    @GetMapping("/getmyarticle")
    public Result<ArrayList<Article>> getmyarticle(String jwttoken)
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
        ArrayList<Article> articles = articleService.getmyarticle(authorname);
        return Result.success(articles);
    }

    @PutMapping("/addarticle")
    public Result addarticle(String jwttoken,String title,String content,String topic)
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
        articleService.addarticle(authorname,title,content,topic);
        return Result.success();
    }

    @PutMapping("/deletearticle")
    public Result deletearticle(String jwttoken,String title,String content)
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
        articleService.deletearticle(authorname,title,content);
        return Result.success();
    }

    @PutMapping("/deleteallarticle")
    public Result deleteallarticle(String jwttoken)
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
        articleService.deleteallarticle(authorname);
        return Result.success();
    }

    @PutMapping("/editarticle")
    public Result editarticle(String jwttoken,String lasttitle,String lastcontent,String title,String content)
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
        articleService.editarticle(authorname,lasttitle,lastcontent,title,content);
        return Result.success();
    }
}
