package com.ef;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
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
        System.out.println("Reading lines from file.");
        List<LogLine> logLines = readLines(arguments.getAccesslog());
        System.out.println("Reading done. Processing lines from file.");
        logLines = processLines(logLines, arguments);
        System.out.println("Processing done. Loading lines into database.");
        loadLines(conn, logLines);
        System.out.println("Upload complete.");
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
            Date finalDate = Utilities.getFinalDate(arguments);
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
        int accessLogAdded = 0;
        try {
            PreparedStatement statement;
            ResultSet resultSet;
            for (Iterator<LogLine> iterator = logLines.iterator(); iterator.hasNext(); ) {
               LogLine logLine = iterator.next();
               String selectAccessLog = "SELECT * FROM accesslog WHERE ipaddress = ? AND Date = ? AND Request = ? AND Status = ? AND UserAgent = ?";
               statement = conn.prepareStatement(selectAccessLog);
               statement.setString(1, logLine.getIpAddress());
               statement.setTimestamp(2, new Timestamp(logLine.getDate().getTime()));
               statement.setString(3, logLine.getRequest());
               statement.setInt(4, logLine.getStatus());
               statement.setString(5, logLine.getUserAgent());
               resultSet = statement.executeQuery();
               if (resultSet.isBeforeFirst()) {
                   System.out.println("Skipping adding access log. Record already exists.");
                   continue;
               }
               String insertAccessLog = "INSERT INTO accesslog (ipaddress, Date, Request, Status, UserAgent) VALUES (?, ?, ?, ?, ?)";
               statement = conn.prepareStatement(insertAccessLog);
               statement.setString(1, logLine.getIpAddress());
               statement.setTimestamp(2, new Timestamp(logLine.getDate().getTime()));
               statement.setString(3, logLine.getRequest());
               statement.setInt(4, logLine.getStatus());
               statement.setString(5, logLine.getUserAgent());
               statement.executeUpdate();
               accessLogAdded++;
               System.out.println("Added record to access logs.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create statement for database connection.");
            Utilities.closeConnection(conn);
            System.exit(-1);
        }
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
