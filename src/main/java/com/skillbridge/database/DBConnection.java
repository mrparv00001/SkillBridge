package org.skillbridge.database;

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

    public static Connection getConnection() {

        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected Successfully!");
            return con;

        } catch (SQLException e) {
            System.out.println("❌ Connection Failed");
            e.printStackTrace();
            return null;
        }
    }
}