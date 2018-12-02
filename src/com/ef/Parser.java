package com.ef;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class contains the main method. It takes in the following args:
 * --startDate: The start date to check for IPs that need a ban.
 * --duration: Accepts Daily or Hourly. The time from the start date
 * to check for IPs that need a ban.
 * --threshold: The amount of log in attempts an IP can make before it
 * is marked as needing a ban.
 * --accesslog: The file that the access log that is to be uploaded to
 * the database is contained in
 * --help:Displays basic information on accepted arguments.
 * --host:Allows you to specify the host of the database.
 * --username: Allows you to specify the username of the database.
 * --password: Allows you to specify the password for the database.
 * It's important to note that if the other three values are not specified,
 * the entire accesslog file will be added to the database, though no check
 * will be done for ips. If a time period is specified, however, only the
 * relevant entries for the check will be added.
 */
public class Parser {
    /**
     * This function takes in the arguments, parses them, and determines what
     * to do based on the arguments.
     * @param args
     */
    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        arguments.parse(args);
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String ssl = "?useSSL=false";
            conn = DriverManager.getConnection(arguments.getHost() + ssl, arguments.getUsername(), arguments.getPassword());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Utilities.closeConnection(conn);
            System.exit(-1);
        }
        if (arguments.getAccesslog().equals("")) {
            if (arguments.getDate() == null || arguments.getDuration().equals("") || arguments.getThreshold() < 0) {
                System.out.println("Invalid arguments. Please provide startdate, duration, and threshold.");
                Utilities.closeConnection(conn);
                System.exit(-1);
            }
            DataChecker.checkData(conn, arguments);
        } else {
            if (arguments.getDate() == null || arguments.getDuration().equals("") || arguments.getThreshold() < 0) {
                System.out.println("Params startdate, duration, and/or threshold were not entered.");
                System.out.println("Uploading data to database, but not checking for banned IPs.");
                DataUploader.uploadData(conn, arguments);
            } else {
                DataUploader.uploadData(conn, arguments);
                DataChecker.checkData(conn, arguments);
            }
        }
        Utilities.closeConnection(conn);
        System.exit(0);
    }
}
