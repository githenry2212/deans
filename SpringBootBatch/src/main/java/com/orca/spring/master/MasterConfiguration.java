package com.orca.spring.master;

import com.orca.spring.beans.CreditBill;
import com.orca.spring.master.handler.RemoteBatchPartitionHandler;
import com.orca.spring.master.partitioner.RemoteBatchPartitioner;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yangfan
 * @since 2017/11/7 9:41
 */
@Configuration
@EnableBatchProcessing
@Import(DataSourceAutoConfiguration.class)
public class MasterConfiguration implements InitializingBean {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    @Bean
    Partitioner partitioner(JdbcTemplate jdbcTemplate) {
        RemoteBatchPartitioner partitioner = new RemoteBatchPartitioner();
        partitioner.setJdbcTemplate(jdbcTemplate);
        return partitioner;
    }

    @Bean
    PartitionHandler partitionHandler(JmsMessagingTemplate jmsTemplate) {
        RemoteBatchPartitionHandler handler = new RemoteBatchPartitionHandler();
        handler.setDestinationQueueName("queue.partition.request");
        handler.setGridSize(100);
        handler.setReplyQueueName("queue.partition.response");
        handler.setStepName("partitionSlaveStep");
        handler.setJmsTemplate(jmsTemplate);
        return handler;
    }

    @Bean("partitionMasterStep")
    Step getStep(Partitioner partitioner, PartitionHandler handler) {
        return stepBuilderFactory.get("partitionMasterStep")
                .partitioner("partitionSlaveStep", partitioner)
                .partitionHandler(handler).build();
    }

    @Bean
    Job getJob(@Qualifier("partitionMasterStep") Step step) {
        return jobBuilderFactory.get("partitionMasterJob").flow(step).end().build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 设置信任的可以序列化的包名
        List<String> packages = new ArrayList<>(1);
        packages.add(CreditBill.class.getPackage().getName());
        packages.add(StepExecution.class.getPackage().getName());
        packages.add(Long.class.getPackage().getName());
        packages.add(Date.class.getPackage().getName());
        packages.add(ExecutionContext.class.getPackage().getName());
        packages.add(Timestamp.class.getPackage().getName());
        connectionFactory.setTrustedPackages(packages);
        // 也可以写成connectionFactory.setTrustAllPackages(true);
    }
}
