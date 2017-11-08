package com.orca.spring.slave.handler;

import com.orca.spring.beans.StepExecutionRequest;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author yangfan
 * @since 2017/11/7 14:24
 */
public class StepExecutionRequestHandler {

    private JobExplorer jobExplorer;
    private StepLocator stepLocator;

    @JmsListener(destination = "queue.partition.request", concurrency = "2")
    @SendTo("queue.partition.response")
    public StepExecution handleRequest(StepExecutionRequest request) {
        Long jobExecutionId = request.getJobExecutionId();
        Long stepExecutionId = request.getStepExecutionId();
        StepExecution stepExecution = jobExplorer.getStepExecution(jobExecutionId, stepExecutionId);
        try {
            Assert.notNull(stepExecution, "StepExecution not exists: " + stepExecutionId);
            String stepName = request.getStepName();
            Step step = stepLocator.getStep(stepName);
            step.execute(stepExecution);
        } catch (JobInterruptedException e) {
            stepExecution.setStatus(BatchStatus.STOPPED);
            // The receiver should update the stepExecution in repository
        } catch (Exception e) {
            stepExecution.addFailureException(e);
            stepExecution.setStatus(BatchStatus.FAILED);
            // The receiver should update the stepExecution in repository
        }
        return stepExecution;
    }

    public JobExplorer getJobExplorer() {
        return jobExplorer;
    }

    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    public StepLocator getStepLocator() {
        return stepLocator;
    }

    public void setStepLocator(StepLocator stepLocator) {
        this.stepLocator = stepLocator;
    }
}
