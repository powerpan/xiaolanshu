package com.xiaolanshu.fitnessGuidance.mapper;

import com.xiaolanshu.fitnessGuidance.pojo.Notice;
import com.xiaolanshu.fitnessGuidance.pojo.Tempnotice;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface NoticeMapper {
    @Select("SELECT * from notices ORDER BY noticetime DESC")
    ArrayList<Notice> getnotice();

    @Select("SELECT * FROM notices WHERE id = #{id}")
    Notice getNoticeDetail(Integer id);

    @Insert("INSERT into notices(title,content,author,noticetime) "+
            "values(#{title},#{content},#{author},now())")
    void addnotice(String author,String title,String content);

    @Delete("DELETE FROM notices WHERE id = #{id}")
    void deleteNoticeById(Integer id);


    @Update("UPDATE notices " +
            "SET title = #{title}, " +
            "content = #{content} " +
            "WHERE id = #{id}")
    void editNoticeById(Integer id, String title, String content);

    @Insert("INSERT into tempnotices(title,content,author,tempnoticetime) "+
            "values(#{title},#{content},#{authorname},now())")
    void savetempnotice(String title, String content, String authorname);

    @Select("SELECT * from tempnotices WHERE author = #{authorname}")
    ArrayList<Tempnotice> gettempnotice(String authorname);

    @Delete("DELETE from tempnotices WHERE author = #{authorname}")
    void deletetempnotice(String authorname);
}
