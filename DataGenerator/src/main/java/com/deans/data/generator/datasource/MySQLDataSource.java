package com.deans.data.generator.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDataSource {

    private String url;
    private String username;
    private String password;

    public MySQLDataSource(String driverClass, String url, String username, String password) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClass + "]", ex);
        }
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
