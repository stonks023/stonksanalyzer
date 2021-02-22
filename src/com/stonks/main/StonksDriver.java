package com.stonks.main;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class StonksDriver {
    public static final String fileLocation = "/Users/sharvika/Desktop/project/Stonks/snp500_trimmed.csv";
    public static final int MODE = 1;
    public static final int NUM_CALLS_PER_MIN_THRESHOLD = 5;

    public static void main(String args[]) {
        List<String> companies = FileReader.readFile(fileLocation);
        System.out.println("Number of companies in the S&P500 file: " + companies.size());

        if (MODE == 0) {
            int counter = 0;
            for (String company : companies) {
                System.out.println(String.format("*********** Working on the company %s **********", company));
                String data = DataProvider.fetchData(company);

                // if storeCompanyData doesn't actually store anything, no need to update the counter
                if (DataProvider.storeCompanyData(company, data)) {
                    counter++;
                }

                try {
                    if (counter > NUM_CALLS_PER_MIN_THRESHOLD) {
                        System.out.println("Got to wait for good things...");
                        Thread.sleep(1000 * 65);
                        counter = 0;
                    }
                } catch (Exception e) {
                    System.err.println("Error while waiting for a min: " + e.getMessage());
                    break;
                }
            }
        } else {
            // this is where we analyze the data
            List<String> outputList = new ArrayList<>();
            for (String company : companies) {
                System.out.println(String.format("*********** Working on the company %s **********", company));
                String data = DataProvider.fetchData(company);
                DataProvider.storeCompanyData(company, data); // we need to store the data in case we didn't have it already
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    CompanyData companyData = objectMapper.readValue(data, CompanyData.class);
                    Map<String, StockData> timeSeriesData = companyData.timeSeriesDataMap;

                    if (StockAnalyzer.analyze(timeSeriesData)) {
                        System.out.println(String.format("Stonks pickers say buy that stock %s", company));
                        outputList.add(company);
                    }

                } catch (IOException e) {
                    System.err.println("Error reading company data file: " + e.getMessage());
                }
            }
            System.out.println(outputList);
        }

    }
}
