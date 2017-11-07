package com.orca.spring.beans;

import java.io.Serializable;

public class StepExecutionRequest implements Serializable {

    private static final long serialVersionUID = -1684089571168233883L;
    private Long stepExecutionId;
    private String stepName;
    private Long jobExecutionId;
    private Integer partitionSize;

    public StepExecutionRequest(Long stepExecutionId, String stepName, Long jobExecutionId, Integer partitionSize) {
        this.stepExecutionId = stepExecutionId;
        this.stepName = stepName;
        this.jobExecutionId = jobExecutionId;
        this.partitionSize = partitionSize;
    }

    public Long getStepExecutionId() {
        return stepExecutionId;
    }

    public void setStepExecutionId(Long stepExecutionId) {
        this.stepExecutionId = stepExecutionId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public Integer getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(Integer partitionSize) {
        this.partitionSize = partitionSize;
    }

    @Override
    public String toString() {
        return String.format("StepExecutionRequest: [jobExecutionId=%d, stepExecutionId=%d, stepName=%s]",
                jobExecutionId, stepExecutionId, stepName);
    }

}
