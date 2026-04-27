package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.Article;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface ArticleMapper {

    @Select("SELECT * from articles WHERE title = #{title} AND topic = #{topic}")
    ArrayList<Article> getarticle(String title, String topic);

    @Select("SELECT * from articles WHERE title = #{title}")
    ArrayList<Article> getarticlewithouttopic(String title);

    @Select("SELECT * from articles WHERE author = #{username}")
    ArrayList<Article> getmyarticle(String username);

    @Insert("INSERT into articles(title,content,author,topic,articletime) "+
            "values(#{title},#{content},#{username},#{topic},now())")
    void addarticle(String username, String title, String content, String topic);

    @Delete("DELETE from articles WHERE author = #{username} AND title =#{title} AND content =#{content}")
    void deletearticle(String username, String title, String content);

    @Delete("DELETE from articles WHERE author = #{username}")
    void deleteallarticle(String username);

    @Update("UPDATE articles " +
            "SET title = #{title}, " +
            "content = #{content} " +
            "WHERE title = #{lasttitle} AND content =#{lastcontent} AND author =#{username} ")
    void editarticle(String username, String lasttitle, String lastcontent, String title, String content);
}
