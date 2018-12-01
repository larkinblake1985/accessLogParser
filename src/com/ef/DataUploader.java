package com.ef;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.sql.Timestamp;

/**
 * This class uploads the data from an accesslog to the database.
 * It checks to see if records have already been entered to prevent
 * duplicate data.
 */
public class DataUploader {

    public static void uploadData(Connection conn, Arguments arguments) {
        List<LogLine> logLines = readLines(arguments.getAccesslog());
        logLines = processLines(logLines, arguments);
        loadLines(conn, logLines);
    }

    /**
     * Checks arguments for a range. If the range is specified by startDate and duration,
     * it returns a list that only falls within that range. Otherwise returns the entire
     * list.
     * @param logLines
     * @param arguments
     * @return
     */
    private static List<LogLine> processLines(List<LogLine> logLines, Arguments arguments) {
        if (arguments.getDate() == null || arguments.getDuration().equals("")) {
            return logLines;
        } else {
            Date finalDate;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(arguments.getDate());
            if (arguments.getDuration().equalsIgnoreCase("daily")) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            } else if (arguments.getDuration().equalsIgnoreCase("hourly")) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }
            finalDate = calendar.getTime();
            for (Iterator<LogLine> iterator = logLines.iterator(); iterator.hasNext();) {
                LogLine logLine = iterator.next();
                if (logLine.getDate().compareTo(arguments.getDate()) < 0 || finalDate.compareTo(logLine.getDate()) < 0) {
                    iterator.remove();
                }
            }
        }
        return logLines;
    }

    /**
     * Loads current lines into the database, checking for duplicates first.
     */
    private static void loadLines(Connection conn, List<LogLine> logLines) {
        int ipAddressesAdded = 0;
        int accessLogAdded = 0;
        try {
            PreparedStatement statement;
            ResultSet resultSet;
            for (Iterator<LogLine> iterator = logLines.iterator(); iterator.hasNext(); ) {
                Integer ipAddress_ID = null;
                LogLine logLine = iterator.next();
                String selectIPAddress = "SELECT * FROM ipaddress WHERE ipaddress = ?";
                statement = conn.prepareStatement(selectIPAddress);
                statement.setString(1, logLine.getIpAddress());
                resultSet = statement.executeQuery();
                if(resultSet.isBeforeFirst()) {
                   resultSet.next();
                   ipAddress_ID = resultSet.getInt("id");

                   String selectAccessLog = "SELECT * FROM accesslog WHERE ipaddress_ID = ? AND Date = ? AND Request = ? AND Status = ? AND UserAgent = ?";
                   statement = conn.prepareStatement(selectAccessLog);
                   statement.setInt(1, ipAddress_ID);
                   statement.setTimestamp(2, new Timestamp(logLine.getDate().getTime()));
                   statement.setString(3, logLine.getRequest());
                   statement.setInt(4, logLine.getStatus());
                   statement.setString(5, logLine.getUserAgent());
                   resultSet = statement.executeQuery();
                   if (resultSet.isBeforeFirst()) {
                       continue;
                   }
                } else {
                    String insertIPAddress = "INSERT INTO ipaddress (ipaddress) VALUES (?)";
                    statement = conn.prepareStatement(insertIPAddress);
                    statement.setString(1, logLine.getIpAddress());
                    statement.executeUpdate();
                    ipAddressesAdded++;
                }
                if (ipAddress_ID == null) {
                    statement = conn.prepareStatement(selectIPAddress);
                    statement.setString(1, logLine.getIpAddress());
                    resultSet = statement.executeQuery();
                    resultSet.next();
                    ipAddress_ID = resultSet.getInt("id");
                }
                String insertAccessLog = "INSERT INTO accesslog (ipaddress_ID, Date, Request, Status, UserAgent) VALUES (?, ?, ?, ?, ?)";
                statement = conn.prepareStatement(insertAccessLog);
                statement.setInt(1, ipAddress_ID);
                statement.setTimestamp(2, new Timestamp(logLine.getDate().getTime()));
                statement.setString(3, logLine.getRequest());
                statement.setInt(4, logLine.getStatus());
                statement.setString(5, logLine.getUserAgent());
                statement.executeUpdate();
                accessLogAdded++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create statement for database connection.");
            System.exit(-1);
        }
        System.out.println(ipAddressesAdded + " IP Addresses added to database.");
        System.out.println(accessLogAdded + " Access Log Records added to database.");
    }

    /**
     * This function takes in the accesslog, attempts to open the file,
     * then attempts to read the file into a List of LogLine objects.
     * @param accesslog
     * @return
     */
    private static List<LogLine> readLines(String accesslog) {
        List<LogLine> logLines = new ArrayList<LogLine>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(accesslog));
            String line = reader.readLine();
            while(line != null) {
                String[] lines = line.split("\\|");
                LogLine logLine = new LogLine(lines[0], lines[1], lines[2], lines[3], lines[4]);
                logLines.add(logLine);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found. Please check your filepath and try again.");
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File contains lines that cannot be read. Please check file for errors.");
            System.exit(-1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Failed to parse date. Please check file for badly formatted dates.");
            System.exit(-1);
        }
        return logLines;
    }
}
