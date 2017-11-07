package com.gzcb.ams.batch.remote.handler;

import com.alibaba.fastjson.JSON;
import com.gzcb.ams.batch.remote.beans.StepExecutionRequest;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.step.NoSuchStepException;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.Assert;

import javax.jms.*;

public class StepExecutionRequestHandler implements MessageListener {

    private JobExplorer jobExplorer;
    private StepLocator stepLocator;
    private JmsTemplate jmsTemplate;
    private String replyQueueName;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println(textMessage.getText());
            StepExecutionRequest request = JSON.parseObject(textMessage.getText(), StepExecutionRequest.class);
            StepExecution execution = handle(request);
            // TODO JMS消息发送健壮性
            jmsTemplate.send(replyQueueName, session -> {
                Message replyMsg = session.createObjectMessage(execution);
                replyMsg.setJMSExpiration(30000);
                return replyMsg;
            });
        } catch (JMSException e) {
            // TODO 异常处理策略
            e.printStackTrace();
        }

    }

    public StepExecution handle(StepExecutionRequest request) {

        Long jobExecutionId = request.getJobExecutionId();
        Long stepExecutionId = request.getStepExecutionId();
        StepExecution stepExecution = jobExplorer.getStepExecution(jobExecutionId, stepExecutionId);
        Assert.notNull(stepExecution);
        try {
            String stepName = request.getStepName();
            Step step = stepLocator.getStep(stepName);
            step.execute(stepExecution);
        } catch (JobInterruptedException e) {
            stepExecution.setStatus(BatchStatus.STOPPED);
            // The receiver should update the stepExecution in repository
        } catch (Throwable e) {
            stepExecution.addFailureException(e);
            stepExecution.setStatus(BatchStatus.FAILED);
            // The receiver should update the stepExecution in repository
        }

        return stepExecution;
    }

    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    public void setStepLocator(StepLocator stepLocator) {
        this.stepLocator = stepLocator;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setReplyQueueName(String replyQueueName) {
        this.replyQueueName = replyQueueName;
    }
}
