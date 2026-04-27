package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.Article;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.ArticleService;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @GetMapping("/getarticle")
    public Result<ArrayList<Article>> getarticle(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken, String title, String topic) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        return Result.success(articleService.getarticle(title, topic));
    }

    @GetMapping("/getarticledetail")
    public Result<Article> getArticleDetail(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken, Integer id) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        Article article = articleService.getArticleDetail(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }

    @GetMapping("/getarticlewithouttopic")
    public Result<ArrayList<Article>> getarticlewithouttopic(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken, String title) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        return Result.success(articleService.getarticlewithouttopic(title));
    }

    @GetMapping("/getmyarticle")
    public Result<ArrayList<Article>> getmyarticle(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        String authorname = String.valueOf(claims.get("username"));
        return Result.success(articleService.getmyarticle(authorname));
    }

    @PutMapping("/addarticle")
    public Result addarticle(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken, String title, String content, String topic) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        if (!isAdmin(claims)) {
            return Result.error("无相关权限");
        }
        String authorname = String.valueOf(claims.get("username"));
        articleService.addarticle(authorname, title, content, topic);
        return Result.success();
    }

    @PutMapping("/deletearticle")
    public Result deletearticle(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken, Integer id, String title, String content) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        if (!isAdmin(claims)) {
            return Result.error("无相关权限");
        }
        if (id != null) {
            articleService.deleteArticleById(id);
        } else {
            String authorname = String.valueOf(claims.get("username"));
            articleService.deletearticle(authorname, title, content);
        }
        return Result.success();
    }

    @PutMapping("/deleteallarticle")
    public Result deleteallarticle(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        if (!isAdmin(claims)) {
            return Result.error("无相关权限");
        }
        String authorname = String.valueOf(claims.get("username"));
        articleService.deleteallarticle(authorname);
        return Result.success();
    }

    @PutMapping("/editarticle")
    public Result editarticle(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            String jwttoken, Integer id, String lasttitle, String lastcontent, String title, String content, String topic) {
        Map<String, Object> claims = parseClaims(resolveToken(authorization, jwttoken));
        if (claims == null) {
            return Result.error("未登录");
        }
        if (!isAdmin(claims)) {
            return Result.error("无相关权限");
        }
        if (id != null) {
            articleService.editArticleById(id, title, content, topic);
        } else {
            String authorname = String.valueOf(claims.get("username"));
            articleService.editarticle(authorname, lasttitle, lastcontent, title, content);
        }
        return Result.success();
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

    private Map<String, Object> parseClaims(String token) {
        try {
            return Jwtutil.parseToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isAdmin(Map<String, Object> claims) {
        User user = userService.findUsername(String.valueOf(claims.get("username")));
        return user != null && Objects.equals(user.getIdentity(), "ADMIN");
    }
}
