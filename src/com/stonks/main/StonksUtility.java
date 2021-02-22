package com.stonks.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StonksUtility {

    public static String urlPathCreator(String company) {
        String url = String.format("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=UJMOXIEI47CX9AGZ", company);
        System.out.println(url);
        return url;
    }

    public static String generateFilePath(String company) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        LocalDateTime now = LocalDateTime.now();
        return "/Users/sharvika/Desktop/project/Stonks2/data_files/" + formatter.format(now) + "/" + company;
    }
}
