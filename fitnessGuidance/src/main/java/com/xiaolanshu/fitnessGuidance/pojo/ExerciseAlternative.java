package com.xiaolanshu.fitnessGuidance.pojo;

import lombok.Data;

@Data
public class ExerciseAlternative {
    private Integer id;
    private Integer sourceGuideId;
    private Integer targetGuideId;
    private String reason;
    private Integer priority;
    private ExerciseGuide sourceGuide;
    private ExerciseGuide targetGuide;
}
