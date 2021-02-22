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

    public static void main(String args[]) {
        List<String> companies = FileReader.readFile(fileLocation);
        System.out.println("Number of companies in the file: " + companies.size());

        if (MODE == 0) {
            int counter = 0;
            for (String company : companies) {
                // TODO: check if the file already exists, override the counter in that case
                String data = DataProvider.fetchData(company);
                if (DataProvider.storeCompanyData(company, data)) {
                    counter++;
                }

                try {
                    if (counter > 5) {
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
                String data = DataProvider.fetchData(company);
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
