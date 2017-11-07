package com.orca.spring.slave;

import com.orca.spring.beans.CreditBill;
import com.orca.spring.slave.mapper.CreditBillRowMapper;
import com.orca.spring.slave.processor.PartitionCreditProcessor;
import com.orca.spring.slave.writer.SystemOutWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

/**
 * @author yangfan
 * @since 2017/11/7 11:11
 */
@Configuration
@EnableBatchProcessing
@Import(DataSourceAutoConfiguration.class)
public class SlaveConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;

    @Bean("partitionSlaveStep")
    Step step(ItemReader<CreditBill> reader, ItemProcessor<CreditBill, CreditBill> processor, ItemWriter<CreditBill> writer) {
        return stepBuilderFactory.get("partitionSlaveStep")
                .<CreditBill, CreditBill>chunk(20)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    ItemReader<CreditBill> itemReader(PagingQueryProvider pagingQueryProvider, RowMapper<CreditBill> rowMapper) {
        JdbcPagingItemReader<CreditBill> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setPageSize(20);
        itemReader.setQueryProvider(pagingQueryProvider);
        itemReader.setRowMapper(rowMapper);
        return itemReader;
    }

    @Bean
    @StepScope
    PagingQueryProvider pagingQueryProvider(@Value("#{stepExecutionContext[min]}") Integer min,
                                            @Value("#{stepExecutionContext[max]}") Integer max) throws Exception {
        SqlPagingQueryProviderFactoryBean providerFactoryBean = new SqlPagingQueryProviderFactoryBean();
        providerFactoryBean.setDataSource(dataSource);
        providerFactoryBean.setSelectClause("select ID,ACCOUNT_ID,NAME,AMOUNT,DATE,ADDRESS");
        providerFactoryBean.setFromClause("from t_credit_bill");
        providerFactoryBean.setWhereClause(String.format("where ID between %s and %s", min, max));
        providerFactoryBean.setSortKey("ID");
        return providerFactoryBean.getObject();
    }

    @Bean
    RowMapper<CreditBill> rowMapper() {
        return new CreditBillRowMapper();
    }

    @Bean
    @StepScope
    ItemProcessor<CreditBill, CreditBill> itemProcessor() {
        return new PartitionCreditProcessor();
    }

    @Bean
    @StepScope
    ItemWriter<CreditBill> itemWriter() {
        return new SystemOutWriter();
    }
}
