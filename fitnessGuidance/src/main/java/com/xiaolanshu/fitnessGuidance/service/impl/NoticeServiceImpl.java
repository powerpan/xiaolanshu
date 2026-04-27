package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.NoticeMapper;
import com.xiaolanshu.fitnessGuidance.pojo.Notice;
import com.xiaolanshu.fitnessGuidance.pojo.Tempnotice;
import com.xiaolanshu.fitnessGuidance.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticemapper;

    @Override
    public ArrayList<Notice> getnotice()
    {
        return noticemapper.getnotice();
    }

    @Override
    public void addnotice(String author,String title,String content)
    {
        noticemapper.addnotice(author,title,content);
    }

    @Override
    public void deletenotice(String title,String content)
    {
        noticemapper.deletenotice(title,content);
    }

    @Override
    public void editnotice(String lasttitle,String lastcontent,String title,String content)
    {
        noticemapper.editnotice(lasttitle,lastcontent,title,content);
    }

    @Override
    public void savetempnotice(String title, String content, String authorname)
    {
        noticemapper.savetempnotice(title,content,authorname);
    }

    @Override
    public ArrayList<Tempnotice> gettempnotice(String authorname)
    {
        return noticemapper.gettempnotice(authorname);
    }

    @Override
    public void deletetempnotice(String authorname) {
        noticemapper.deletetempnotice(authorname);
    }
}
