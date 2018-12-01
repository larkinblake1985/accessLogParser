package com.ef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Arguments {
    //The accesslog of attempted logins
    private String accesslog = "";
    //The date to start checking IPs
    private Date date = null;
    //The time length to check IPs
    private String duration = "";
    //Number of login attempts before IPs are banned
    private int threshold = -1;
    //Database host
    private String host = "jdbc:mysql://localhost:3306/accesslog";
    //Database username
    private String username = "root";
    //Database password
    private String password = "1I2heart3SQL4!";

    /**
     * This function takes in the args object from main and attempts to parse it.
     * It stores the results in the variables contained in the class object, which can
     * then be used to access all of the arguments in the code.
     * @param args
     */
    public void parse(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                String[] values = args[i].split("=");
                switch (values[0]) {
                    case ("--accesslog"):
                        accesslog = values[1];
                        break;
                    case ("--startDate"):
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
                        date = format.parse(values[1]);
                        break;
                    case("--duration"):
                        duration = values[1];
                        if (!duration.equalsIgnoreCase("hourly") && !duration.equalsIgnoreCase("daily")) {
                            System.out.println("Value: " + duration + " is not a valid duration.");
                            System.exit(-1);
                        }
                        break;
                    case("--threshold"):
                        threshold = Integer.parseInt(values[1]);
                        break;
                    case("--help"):
                        Help.help();
                        break;
                    case("--host"):
                        host = values[1];
                        break;
                    case("--username"):
                        username = values[1];
                        break;
                    case("--password"):
                        password = values[1];
                        break;
                    default:
                        System.out.println("Invalid Argument " + values[0]);
                        break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Failed to parse date. Please check your start date input.");
            System.exit(-1);
        }
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
