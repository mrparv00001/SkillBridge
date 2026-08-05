package com.skillbridge;

import com.skillbridge.database.DBConnection;
import com.skillbridge.menu.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println(" Welcome to SkillBridge Platform");
        System.out.println(" Peer Learning & Skill Exchange System");
        System.out.println("==========================================");

        // Test database connection on startup
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {

            } else {
                System.err.println("Failed to connect to the database.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            System.err.println("Make sure XAMPP Apache and MySQL are running!");
            return;
        }

        // Launch the application Main Menu
        MainMenu mainMenu = new MainMenu();
        mainMenu.start();
    }
}