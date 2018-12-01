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
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from ipaddress");
            while(rs.next()) {
                System.out.println(rs.getInt(1) + "  " + rs.getString(2));
            }
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

        } else {

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
