package com.example.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail imgJobDetail() {
        return JobBuilder.newJob(ImgQuartzJob.class)
                .withIdentity("imgJob", "imgGroup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger imgJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(imgJobDetail())
                .withIdentity("imgTrigger", "imgGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
                .build();
    }
}