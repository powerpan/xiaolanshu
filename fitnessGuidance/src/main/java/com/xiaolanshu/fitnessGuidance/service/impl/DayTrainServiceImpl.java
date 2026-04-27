package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.DayTrainMapper;
import com.xiaolanshu.fitnessGuidance.pojo.DayTrain;
import com.xiaolanshu.fitnessGuidance.service.DayTrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DayTrainServiceImpl implements DayTrainService {

    @Autowired
    private DayTrainMapper dayTrainMapper;

    @Override
    public DayTrain getdaytrain(String splitMode){
        return dayTrainMapper.getdaytrain(splitMode);
    }

}
