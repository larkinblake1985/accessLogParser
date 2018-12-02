package com.ef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogLine {
    //The Date in the log record.
    private Date date;
    //The ip address in the log record.
    private String ipAddress;
    //The request in the log record.
    private String request;
    //The status in the log record.
    private int status;
    //The user agent string in the log record.
    private String userAgent;

    /**
     * Takes in the raw data from the log, parses it, and stores it in the object.
     * @param date
     * @param ipAddress
     * @param request
     * @param status
     * @param userAgent
     */
    public LogLine(String date, String ipAddress, String request, String status, String userAgent) throws ParseException {
        this.ipAddress = ipAddress;
        this.request = request;
        this.userAgent = userAgent;
        this.status = Integer.parseInt(status);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.date = format.parse(date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
