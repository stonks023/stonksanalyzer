package com.stonks.main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataProvider {
    public static String fetchData(String company) {
        // check if data already exists for the current day
        if (dataExistsForCompany(company)) {
            System.out.println(String.format("Looks like the data already exists for %s", company));
            return FileReader.readExistingFile(StonksUtility.generateFilePath(company));
        }

        System.out.println(String.format("Data not found for %s, going to download it", company));

        StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL(StonksUtility.urlPathCreator(company));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");

            // Alphavantage sends 200 for throttled calls as well
            while ((output = br.readLine()) != null) {
                builder.append(output + System.lineSeparator());
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }

        /*
        need to check if we are getting throttled
        {
            "Note": "Thank you for using Alpha Vantage! Our standard API call frequency is 5 calls per minute and 500 calls per day.
            Please visit https://www.alphavantage.co/premium/ if you would like to target a higher API call frequency."
        }
         */
        String returnData = builder.toString();
        if (returnData.contains("Thank you for using Alpha Vantage")) {
            return "";
        } else {
            return returnData;
        }
    }

    public static boolean storeCompanyData(String company, String data) {
        // TODO: Don't need to have folder for date
        if (dataExistsForCompany(company) || data.isEmpty()) {
            System.out.println(String.format("Data either already exists for company %s or is empty: %b", company, data.isEmpty()));
            return false;
        }

        try {
            // Create file
            File companyFile = new File(StonksUtility.generateFilePath(company));
            FileWriter fstream = new FileWriter(companyFile);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(data);
            out.close();
            System.out.println(String.format("Data stored for company %s at %s", company, StonksUtility.generateFilePath(company)));
            return true;
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        return false;
    }

    private static boolean dataExistsForCompany(String company) {
        File fileChecker = new File(StonksUtility.generateFilePath(company));
        return fileChecker.exists() && !fileChecker.isDirectory();
    }
}
