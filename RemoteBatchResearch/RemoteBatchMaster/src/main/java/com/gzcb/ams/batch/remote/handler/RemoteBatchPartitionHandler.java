package com.gzcb.ams.batch.remote.handler;

import com.alibaba.fastjson.JSON;
import com.gzcb.ams.batch.remote.beans.StepExecutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.StepExecutionSplitter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class RemoteBatchPartitionHandler implements PartitionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteBatchPartitionHandler.class);
    private static final int MAX_REDELIVER_COUNT = 10;

    private JmsTemplate jmsTemplate;
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
            jmsTemplate.send(destinationQueueName, session -> session.createTextMessage(JSON.toJSONString(request)));
        }
        // TODO 接收响应的超时策略
        return receiveReplies(masterStepExecution.getJobExecutionId(), splitSize);
    }


    private Collection<StepExecution> receiveReplies(long jobExecutionId, int splitSize) throws JMSException {
        Collection<StepExecution> replies = new ArrayList<>(splitSize);
        do {
            Message message = jmsTemplate.receive(replyQueueName);
            if(message == null){
                break;
            }
            ObjectMessage objectMessage = (ObjectMessage) message;
            StepExecution result = (StepExecution) objectMessage.getObject();
            if (result.getJobExecutionId() == jobExecutionId) {
                replies.add(result);
                continue;
            }
            Object redeliverCountObj = objectMessage.getObjectProperty("redeliverCount");
            if (redeliverCountObj == null) {
                // objectMessage只读
//                objectMessage.setIntProperty("redeliverCount", 1);
                // 重新投递进队列
                redelivered(objectMessage);
            } else {
                // redeliverCountObj must be instance of the Integer
//                int redeliverCount = Integer.class.cast(redeliverCountObj);
//                if(redeliverCount < MAX_REDELIVER_COUNT){
//                    objectMessage.setIntProperty("redeliverCount", ++redeliverCount);
//                    // 重新投递进队列
//                    redelivered(objectMessage);
//                }
            }

        } while (replies.size() < splitSize);
        return replies;

    }

    private void redelivered(ObjectMessage objectMessage) {
        jmsTemplate.send(replyQueueName, session -> objectMessage);
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
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
