package com.orca.spring.slave.mapper;


import com.orca.spring.beans.CreditBill;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditBillRowMapper implements RowMapper<CreditBill> {

    @Override
    public CreditBill mapRow(ResultSet rs, int rowNum) throws SQLException {
        CreditBill bill = new CreditBill();
        bill.setId(rs.getString("ID"));
        bill.setAccountID(rs.getString("ACCOUNT_ID"));
        bill.setAddress(rs.getString("ADDRESS"));
        bill.setAmount(rs.getDouble("AMOUNT"));
        bill.setDate(rs.getString("DATE"));
        bill.setName(rs.getString("NAME"));
        return bill;
    }
}
