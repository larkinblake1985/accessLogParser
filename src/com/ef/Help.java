package com.ef;

/**
 * This class outputs the help text to the console then exits.
 */
public class Help {
    public static void help() {
        System.out.println("Arguments include:");
        System.out.println("--accesslog=/path/to/log");
        System.out.println("Accesslog is the path to the log file. Default empty string ");
        System.out.println("(doesn't read in a new file, just reads from the database)");
        System.out.println("--startdate=yyyy-MM-dd.HH:mm:ss");
        System.out.println("Start date is the date and time to start checking for connections. ");
        System.out.println("Default null (only allowed for reading file, not for checking IPs)");
        System.out.println("--duration=hourly");
        System.out.println("Duration can be daily or hourly. This is the duration to check ");
        System.out.println("for connections. Default empty string (only allowed for reading file,");
        System.out.println(" not for checking IPs)");
        System.out.println("--threshold=200");
        System.out.println("Integer value of limit of connection attempts before ip address ");
        System.out.println("is banned. Default -1 (only allowed for reading file, not for checking IPs).");
        System.out.println("--host=jdbc:mysql://localhost:3306/accesslog");
        System.out.println("Specifies the host database to connect to.");
        System.out.println("--username=root");
        System.out.println("Specifies the username used to connect to the database.");
        System.out.println("--password=password");
        System.out.println("Specifies the password used to connect to the database.");
        System.exit(0);
    }
}
