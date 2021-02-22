package com.stonks.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class StockAnalyzer {
    private static final int MAX_LOOKBACK_WINDOW = 6;
    private static final double WEEKLY_CHANGE_MIN_THRESHOLD = 5.0;
    private static final double DAILY_CHANGE_MIN_THRESHOLD = 2.0;

    public static boolean analyze(Map<String, StockData> timeSeriesData) {
        Set<String> keys = timeSeriesData.keySet();
        double[] closingPricesDesc = new double[MAX_LOOKBACK_WINDOW];

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0, stocksFound = 0; i < keys.size() && stocksFound < MAX_LOOKBACK_WINDOW; i++) {
            String date = formatter.format(now.minusDays(i));
            if (keys.contains(date)) {
                closingPricesDesc[stocksFound++] = Double.valueOf(timeSeriesData.get(date).close);
            }
        }

        for (int i = 0; i < MAX_LOOKBACK_WINDOW; i++) {
            System.out.println(String.format("Closing price: %s", closingPricesDesc[i]));
        }

        // weekly change
        double weeklyPercentChange = ((closingPricesDesc[0] - closingPricesDesc[MAX_LOOKBACK_WINDOW - 1]) * 100)/closingPricesDesc[MAX_LOOKBACK_WINDOW - 1];

        // weekly not matching minimum threshold
        if (weeklyPercentChange < WEEKLY_CHANGE_MIN_THRESHOLD) {
            System.out.println(String.format("Weekly change of %.2f, is below our threshold of %.2f", weeklyPercentChange, WEEKLY_CHANGE_MIN_THRESHOLD));
            return false;
        }

        System.out.println(String.format("Weekly change of %.2f, is above our threshold of %.2f", weeklyPercentChange, WEEKLY_CHANGE_MIN_THRESHOLD));

        // check for daily percentage change
        double[] dailyPercentChange = new double[MAX_LOOKBACK_WINDOW - 1];
        for (int  i = 0, index = 0; i < MAX_LOOKBACK_WINDOW - 1; i++) {
            dailyPercentChange[index] = ((closingPricesDesc[i] - closingPricesDesc[i+1]) * 100)/closingPricesDesc[i+1];

            if (dailyPercentChange[index] < 0  && Math.abs(dailyPercentChange[index]) > DAILY_CHANGE_MIN_THRESHOLD) {
                System.out.println(String.format("Daily change of %.2f, is below our threshold of %.2f", dailyPercentChange[index], DAILY_CHANGE_MIN_THRESHOLD));
                return false;
            }
            System.out.println(String.format("Daily change of %.2f, is above our threshold of %.2f", dailyPercentChange[index] , DAILY_CHANGE_MIN_THRESHOLD));
            index++;
        }

        return true;
    }
}
