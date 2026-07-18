package com.skillbridge;

import com.skillbridge.database.DBConnection;

public class Main {

    // Just added 'throws Exception' here to handle the red line
    public static void main(String[] args) throws Exception {

        DBConnection.getConnection();

    }
}