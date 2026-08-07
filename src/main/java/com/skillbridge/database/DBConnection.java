package com.skillbridge.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Flag to track environment (defaults to online)
    private static boolean useLocalhost = false;

    // --- ONLINE CREDENTIALS (TiDB Cloud) ---
    private static final String ONLINE_URL =
            "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/skillbridge?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3";
    private static final String ONLINE_USER = "24cPrP7jfNiAnuf.root";
    private static final String ONLINE_PASSWORD = "S1EnSarxsg8oMhzT";

    // --- LOCALHOST CREDENTIALS (XAMPP Default) ---
    private static final String LOCAL_URL = "jdbc:mysql://localhost:3306/skillbridge";
    private static final String LOCAL_USER = "root";
    private static final String LOCAL_PASSWORD = ""; // Default XAMPP password is empty

    // Method to set environment before getting connection
    public static void setUseLocalhost(boolean isLocal) {
        useLocalhost = isLocal;
    }

    public static Connection getConnection() throws SQLException {
        // Dynamically select credentials based on user's choice
        String url = useLocalhost ? LOCAL_URL : ONLINE_URL;
        String user = useLocalhost ? LOCAL_USER : ONLINE_USER;
        String password = useLocalhost ? LOCAL_PASSWORD : ONLINE_PASSWORD;

        try {
            Connection con = DriverManager.getConnection(url, user, password);
          //System.out.println("✅ Connected Successfully to " + (useLocalhost ? "Localhost" : "Online Server") + "!");
          return con;
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed!");
            System.err.println("Reason: " + e.getMessage());
            System.err.println("State: " + e.getSQLState());
            throw e;
        }
    }
}