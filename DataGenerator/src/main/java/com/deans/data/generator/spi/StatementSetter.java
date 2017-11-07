package com.deans.data.generator.spi;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementSetter<T> {

    void setValues(T item, PreparedStatement ps) throws SQLException;
}
