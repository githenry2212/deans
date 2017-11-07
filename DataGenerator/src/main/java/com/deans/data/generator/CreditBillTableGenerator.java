package com.deans.data.generator;

import com.deans.data.generator.beans.CreditBill;
import com.deans.data.generator.common.IOUtils;
import com.deans.data.generator.context.GeneratorContext;
import com.deans.data.generator.datasource.MySQLDataSource;
import com.deans.data.generator.spi.BeanGenerator;
import com.deans.data.generator.spi.StatementSetter;
import com.deans.data.generator.spi.impl.CreditBillBeanGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreditBillTableGenerator {

    public static final int EACH_BATCH_SIZE = 100;

    public static void main(String[] args) {
        String insertSql = "insert into t_credit_bill(account_id,name,amount,date,address) values(?,?,?,?,?)";
        GeneratorContext context = new GeneratorContext();
        context.setSql(insertSql);
        context.setBeanGenerator(new CreditBillBeanGenerator());
        context.setGenerateCount(1024);
        context.setStatementSetter((StatementSetter<CreditBill>) (item, ps) -> {
            ps.setString(1, item.getAccountID());
            ps.setString(2, item.getName());
            ps.setDouble(3, item.getAmount());
            ps.setString(4, item.getDate());
            ps.setString(5, item.getAddress());
        });
        String driverClass = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/ams?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false";
        String username = "root";
        String password = "1234";
        MySQLDataSource dataSource = new MySQLDataSource(driverClass, url, username, password);
        new CreditBillTableGenerator().generate(dataSource, context);
    }

    @SuppressWarnings("unchecked")
    public void generate(MySQLDataSource dataSource, GeneratorContext context) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(context.getSql());
            StatementSetter statementSetter = context.getStatementSetter();
            BeanGenerator generator = context.getBeanGenerator();
            int max = context.getGenerateCount();
            for (int count = 1; count <= max; count++) {
                statementSetter.setValues(generator.generate(), ps);
                ps.addBatch();
                if (count % 100 == 0) {
                    ps.executeBatch();
                }
            }
            if (max % 100 != 0) {
                ps.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeAll(ps, connection);
        }
    }

}
