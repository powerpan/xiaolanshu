package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.ActionDetailMapper;
import com.xiaolanshu.fitnessGuidance.pojo.ActionDetail;
import com.xiaolanshu.fitnessGuidance.service.ActionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionDetailServiceImpl implements ActionDetailService {

    @Autowired
    private ActionDetailMapper actionDetailMapper;

    @Override
    public ActionDetail getactiondetail(String actionPattern, String equipment){
       return actionDetailMapper.getactiondetail(actionPattern,equipment);
    }
}
