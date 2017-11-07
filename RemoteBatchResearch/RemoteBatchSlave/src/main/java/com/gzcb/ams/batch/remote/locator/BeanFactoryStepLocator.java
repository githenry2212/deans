package com.gzcb.ams.batch.remote.locator;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;

public class BeanFactoryStepLocator implements StepLocator, BeanFactoryAware {

    private BeanFactory beanFactory;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public Step getStep(String stepName) {
        return beanFactory.getBean(stepName, Step.class);
    }

    public Collection<String> getStepNames() {
        Assert.state(beanFactory instanceof ListableBeanFactory, "BeanFactory is not listable.");
        return Arrays.asList(((ListableBeanFactory) beanFactory).getBeanNamesForType(Step.class));
    }

}
