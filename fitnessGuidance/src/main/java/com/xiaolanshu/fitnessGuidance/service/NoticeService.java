package com.xiaolanshu.fitnessGuidance.service;

import com.xiaolanshu.fitnessGuidance.pojo.Notice;
import com.xiaolanshu.fitnessGuidance.pojo.Tempnotice;

import java.util.ArrayList;

public interface NoticeService {
    ArrayList<Notice> getnotice();

    Notice getNoticeDetail(Integer id);

    void addnotice(String author,String title,String content);

    void deleteNoticeById(Integer id);

    void editNoticeById(Integer id,String title,String content);

    void savetempnotice(String title, String content, String authorname);

    ArrayList<Tempnotice> gettempnotice(String authorname);

    void deletetempnotice(String authorname);
}
