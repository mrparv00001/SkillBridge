package com.skillbridge;

import com.skillbridge.database.DBConnection;
import com.skillbridge.menu.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println(" Welcome to SkillBridge Platform");
        System.out.println(" Peer Learning & Skill Exchange System");
        System.out.println("==========================================");

        // --- Server Selection Menu ---
        System.out.println("\nSelect Database Environment:");
        System.out.println("1. Localhost Server (XAMPP - Testing)");
        System.out.println("2. Online Server (TiDB Cloud - Final Update)");
        System.out.print("Enter choice (1 or 2): ");

        String envChoice = scanner.nextLine();

        if (envChoice.equals("1")) {
            DBConnection.setUseLocalhost(true);
            System.out.println("🔄 Configuring for Localhost...");
        } else {
            DBConnection.setUseLocalhost(false);
            System.out.println("🔄 Configuring for Online Server...");
        }

        // Test database connection on startup
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection established successfully!");
            } else {
                System.err.println("❌ Failed to connect to the database.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            if (envChoice.equals("1")) {
                System.err.println("💡 Make sure XAMPP Apache and MySQL are running!");
            }
            return;
        }

        // Launch the application Main Menu
        MainMenu mainMenu = new MainMenu();
        mainMenu.start();
    }
}