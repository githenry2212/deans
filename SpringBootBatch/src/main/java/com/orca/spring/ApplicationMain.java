package com.orca.spring;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author yangfan
 */
@SpringBootApplication
public class ApplicationMain {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ApplicationMain.class, args);
        JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
        Job job = applicationContext.getBean(Job.class);
        try {
            JobExecution result = jobLauncher.run(job, new JobParametersBuilder().addLong("TIMESTAMP", System.currentTimeMillis()).toJobParameters());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
