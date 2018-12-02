package com.ef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This class checks the table in the specified range and for the specified
 * threshold. It then blocks any IPs found in the range, noting the start date
 * and commenting on the threshold and range given as arguments.
 */
public class DataChecker {

    public static void checkData(Connection conn, Arguments arguments) {
        System.out.println("Getting logs for the selected range.");
        List<String> ipAddresses = getLogsForRange(conn, arguments);
        System.out.println("Logs retrieved.");
        addIPAddressesToBlockList(conn, ipAddresses, arguments);
    }

    private static void addIPAddressesToBlockList(Connection conn, List<String> ipAddresses, Arguments arguments) {
        PreparedStatement statement;
        String selectSql = "SELECT * FROM blockedrecord WHERE ipaddress = ? AND comment = ? and date = ?";
        String insertSql = "INSERT INTO blockedrecord (ipaddress, comment, date) VALUES (?, ?, ?)";
        String comment = "Blocked due to " + arguments.getThreshold() + " appearances in " + arguments.getDuration() + " period.";
        try {
            for (String ipaddress : ipAddresses) {
                statement = conn.prepareStatement(selectSql);
                statement.setString(1, ipaddress);
                statement.setString(2, comment);
                statement.setTimestamp(3, new Timestamp(arguments.getDate().getTime()));
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.isBeforeFirst()) {
                    continue;
                }
                statement = conn.prepareStatement(insertSql);
                statement.setString(1, ipaddress);
                statement.setString(2, comment);
                statement.setTimestamp(3, new Timestamp(arguments.getDate().getTime()));
                statement.executeUpdate();
                System.out.println(ipaddress + " " + comment + " starting at " + arguments.getDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating sql statement to submit IPAddresses to blockrecord table.");
            Utilities.closeConnection(conn);
            System.exit(-1);
        }
    }

    /**
     * Searches the database in the specified range of time and returns only violating IPAddress IDs
     * for the records within that time.
     * @param conn
     * @param arguments
     * @return
     */
    private static List<String> getLogsForRange(Connection conn, Arguments arguments) {
        List<String> ipAddresses = new ArrayList<>();
        PreparedStatement statement;
        try {
            String selectAccessLog = "SELECT t1.ipaddress FROM " +
                    "(SELECT ipaddress FROM accesslog WHERE Date BETWEEN ? AND ?) AS t1" +
                    " GROUP BY t1.ipaddress HAVING COUNT(t1.ipaddress) > ?";
            statement = conn.prepareStatement(selectAccessLog);
            statement.setTimestamp(1, new Timestamp(arguments.getDate().getTime()));
            statement.setTimestamp(2, new Timestamp(Utilities.getFinalDate(arguments).getTime()));
            statement.setInt(3, arguments.getThreshold());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ipAddresses.add(resultSet.getString("ipaddress"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("There was an error creating a SQL statement to read logs.");
            Utilities.closeConnection(conn);
            System.exit(-1);
        }
        return ipAddresses;
    }
}
