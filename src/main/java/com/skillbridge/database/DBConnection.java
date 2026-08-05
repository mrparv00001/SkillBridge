package com.skillbridge.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // LOCALHOST XAMPP Configuration
    private static final String URL = "jdbc:mysql://localhost:3306/skillbridge";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP default is empty

    public static Connection getConnection() throws SQLException {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            // Silent connection - no print statement
            return con;
        } catch (SQLException e) {
            System.err.println("Database Connection Failed!");
            System.err.println("Reason: " + e.getMessage());
            System.err.println("TIP: Make sure XAMPP MySQL is running!");
            throw e;
        }
    }
}