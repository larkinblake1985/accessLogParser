package com.ef;

import java.sql.*;

public class Parser {

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        arguments.parse(args);
        String host = "jdbc:mysql://localhost:3306/accesslog?useSSL=false";
        String username = "root";
        String password = "1I2heart3SQL4!";
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(host, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            closeConnection(conn);
            System.exit(-1);
        }
        if (arguments.accesslog.equals("")) {
            if (arguments.date == null || arguments.duration == "" || arguments.threshold < 0) {
                System.out.println("Invalid arguments. Please provide startdate, duration, and threshold.");
                closeConnection(conn);
                System.exit(-1);
            }
            DataChecker.checkData(conn, arguments);
        } else {
            if (arguments.date == null || arguments.duration == "" || arguments.threshold < 0) {
                System.out.println("Params startdate, duration, and/or threshold were not entered.");
                System.out.println("Uploading data to database, but not checking for banned IPs.");
                DataUploader.uploadData(conn, arguments);
            } else {
                DataUploader.uploadData(conn, arguments);
                DataChecker.checkData(conn, arguments);
            }
        }
        closeConnection(conn);
        System.exit(0);
    }

    private static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
