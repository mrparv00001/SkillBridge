package com.skillbridge.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/skillbridge?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3";

    private static final String USER =
            "24cPrP7jfNiAnuf.root";

    private static final String PASSWORD =
            "S1EnSarxsg8oMhzT";

    // Notice I added 'throws SQLException' here
    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
        // We only print this once to test. You can remove it later so it doesn't spam the console!
        // System.out.println("✅ Connected Successfully!");
        return con;
    }
}