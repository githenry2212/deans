package com.orca.spring.master.handler;

import com.orca.spring.beans.StepExecutionRequest;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.StepExecutionSplitter;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class RemoteBatchPartitionHandler implements PartitionHandler {

    private JmsMessagingTemplate jmsTemplate;
    private String stepName;
    private String destinationQueueName;
    private String replyQueueName;
    private int gridSize = 1;

    @Override
    public Collection<StepExecution> handle(StepExecutionSplitter stepSplitter, StepExecution masterStepExecution)
            throws Exception {
        final Set<StepExecution> split = stepSplitter.split(masterStepExecution, gridSize);
        int splitSize = split.size();
        if (splitSize == 0) {
            return null;
        }
        for (StepExecution stepExecution : split) {
            StepExecutionRequest request = new StepExecutionRequest(stepExecution.getId(), stepName,
                    stepExecution.getJobExecutionId(), splitSize);
            // TODO JMS消息发送时的健壮性
            jmsTemplate.convertAndSend(destinationQueueName, request);
        }
        // TODO 接收响应的超时策略
        return receiveReplies(masterStepExecution.getJobExecutionId(), splitSize);
    }


    private Collection<StepExecution> receiveReplies(long jobExecutionId, int splitSize) {
        Collection<StepExecution> replies = new ArrayList<>(splitSize);
        do {
            StepExecution result = jmsTemplate.receiveAndConvert(replyQueueName, StepExecution.class);
            if (result == null) {
                break;
            }
            if (result.getJobExecutionId() == jobExecutionId) {
                replies.add(result);
            }
        } while (replies.size() < splitSize);
        return replies;

    }

    public void setJmsTemplate(JmsMessagingTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public void setDestinationQueueName(String destinationQueueName) {
        this.destinationQueueName = destinationQueueName;
    }

    public void setReplyQueueName(String replyQueueName) {
        this.replyQueueName = replyQueueName;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
}
