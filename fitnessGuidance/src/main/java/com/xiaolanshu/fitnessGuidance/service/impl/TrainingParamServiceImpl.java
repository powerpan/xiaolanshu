package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.TrainingParamMapper;
import com.xiaolanshu.fitnessGuidance.pojo.TrainingParam;
import com.xiaolanshu.fitnessGuidance.service.TrainingParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingParamServiceImpl implements TrainingParamService {

    @Autowired
    private TrainingParamMapper trainingParamMapper;

    @Override
    public TrainingParam gettrainingparam(String actionPattern, String exLevel){
        return trainingParamMapper.gettrainingparam(actionPattern,exLevel);
    };

}
