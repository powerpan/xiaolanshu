package com.xiaolanshu.fitnessGuidance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FitnessGuidanceApplication {

    public static void main(String[] args) {

        SpringApplication.run(FitnessGuidanceApplication.class, args);
        System.out.println("67910begin");
    }

}
