package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.ArticleMapper;
import com.xiaolanshu.fitnessGuidance.pojo.Article;
import com.xiaolanshu.fitnessGuidance.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public ArrayList<Article> getarticle(String title, String topic) {
       return articleMapper.getarticle(title,topic);
    }

    @Override
    public Article getArticleDetail(Integer id) {
        return articleMapper.getArticleDetail(id);
    }

    @Override
    public ArrayList<Article> getarticlewithouttopic(String title) {
        return articleMapper.getarticlewithouttopic(title);
    }

    @Override
    public ArrayList<Article> getmyarticle(String username) {
        return articleMapper.getmyarticle(username);
    }

    @Override
    public void addarticle(String username, String title, String content,String topic) {
        articleMapper.addarticle(username,title,content,topic);
    }

    @Override
    public void deletearticle(String username, String title, String content) {
        articleMapper.deletearticle(username,title,content);
    }

    @Override
    public void deleteArticleById(Integer id) {
        articleMapper.deleteArticleById(id);
    }

    @Override
    public void deleteallarticle(String username) {
        articleMapper.deleteallarticle(username);
    }

    @Override
    public void editarticle(String username, String lasttitle, String lastcontent, String title, String content) {
        articleMapper.editarticle(username,lasttitle,lastcontent,title,content);
    }

    @Override
    public void editArticleById(Integer id, String title, String content, String topic) {
        articleMapper.editArticleById(id,title,content,topic);
    }

}
