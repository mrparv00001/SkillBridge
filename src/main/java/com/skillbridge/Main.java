package com.skillbridge;

import com.skillbridge.database.DBConnection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            DBConnection.getConnection();
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            System.out.println("Connection failed!");
        }
    }
}