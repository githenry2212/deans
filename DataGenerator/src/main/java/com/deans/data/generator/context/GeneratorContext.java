package com.deans.data.generator.context;

import com.deans.data.generator.spi.BeanGenerator;
import com.deans.data.generator.spi.StatementSetter;

public class GeneratorContext {
    private String sql;
    private BeanGenerator beanGenerator;
    private int generateCount;
    private StatementSetter statementSetter;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public BeanGenerator getBeanGenerator() {
        return beanGenerator;
    }

    public void setBeanGenerator(BeanGenerator beanGenerator) {
        this.beanGenerator = beanGenerator;
    }

    public int getGenerateCount() {
        return generateCount;
    }

    public void setGenerateCount(int generateCount) {
        this.generateCount = generateCount;
    }

    public StatementSetter getStatementSetter() {
        return statementSetter;
    }

    public void setStatementSetter(StatementSetter statementSetter) {
        this.statementSetter = statementSetter;
    }
}
