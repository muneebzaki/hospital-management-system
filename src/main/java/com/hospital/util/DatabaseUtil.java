package com.hospital.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12777954";
    private static final String DB_USER = "sql12777954";
    private static final String DB_PASSWORD = "TEeWUtuReF"; // freesqldatabase.com password

    private static DatabaseUtil instance;
    
    private DatabaseUtil() {
        // Private constructor to prevent instantiation
    }
    
    public static DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }
    
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
} 