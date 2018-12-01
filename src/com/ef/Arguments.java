package com.ef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Arguments {
    String accesslog = "";
    Date date = null;
    String duration = "";
    int threshold = -1;

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
                        break;
                    case("--threshold"):
                        threshold = Integer.parseInt(values[1]);
                        break;
                    case("--help"):
                        Help.help();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Failed to parse date. Please check your start date input.");
            System.exit(-1);
        }
    }
}
