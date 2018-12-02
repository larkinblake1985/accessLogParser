package com.ef;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * A class for static utilities that are called multiple places in the code.
 */
public class Utilities {

    /**
     * This method attempts to close the connection passed in.
     * If it fails, it prints a stack trace.
     * @param conn : Connection object to close.
     */
    public static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the datetime after the duration from the startdate in the arguments.
     * @param arguments
     * @return
     */
    public static Date getFinalDate(Arguments arguments) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(arguments.getDate());
        if (arguments.getDuration().equalsIgnoreCase("daily")) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } else if (arguments.getDuration().equalsIgnoreCase("hourly")) {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        return calendar.getTime();
    }
}
