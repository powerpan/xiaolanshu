package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.Article;
import com.xiaolanshu.fitnessGuidance.pojo.Result;

import java.util.ArrayList;

public interface ArticleService {

    ArrayList<Article> getarticle(String title, String topic);

    Article getArticleDetail(Integer id);

    ArrayList<Article> getarticlewithouttopic(String title);

    ArrayList<Article> getmyarticle(String username);

    void addarticle(String username,String title,String content,String topic);

    void deletearticle(String username,String title,String content);

    void deleteArticleById(Integer id);

    void deleteallarticle(String username);

    void editarticle(String username,String lasttitle,String lastcontent,String title,String content);

    void editArticleById(Integer id,String title,String content,String topic);
}
