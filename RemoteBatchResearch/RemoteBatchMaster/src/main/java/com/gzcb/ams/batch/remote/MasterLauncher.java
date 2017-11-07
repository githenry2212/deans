package com.gzcb.ams.batch.remote;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class MasterLauncher {

    public static void main(String[] args) {
        AbstractXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:partition-master.xml");
        try {
            JobLauncher launcher = context.getBean("jobLauncher", JobLauncher.class);
            Job job = context.getBean("remotePartitionJob", Job.class);
            JobExecution jobExecution = launcher.run(job, new JobParametersBuilder().
                    addLong("currentTime", 1506475229752L).toJobParameters());
            System.out.println(jobExecution);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.destroy();
            context.close();
        }
        /*JobExplorer explorer = context.getBean(JobExplorer.class);
        JobInstance instance = explorer.getJobInstance(16L);
        System.out.println(instance);
        List<JobExecution> executions = explorer.getJobExecutions(instance);
        for (JobExecution execution :
                executions) {
            System.out.println(execution);
        }*/
    }
}
