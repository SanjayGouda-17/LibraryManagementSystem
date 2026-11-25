package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to establish and manage MySQL Database Connections.
 * Uses JDBC DriverManager and MySQL Connector/J driver.
 */
public class DBConnection {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Sanjay*17";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Static block to register the MySQL JDBC Driver once when class is loaded
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new connection to the MySQL database.
     * Callers must close the returned connection using try-with-resources.
     * 
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Helper method to safely close database resources (Connection, Statement, ResultSet).
     * Useful when try-with-resources is not directly applicable.
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception e) {
                    System.err.println("Warning: Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}
