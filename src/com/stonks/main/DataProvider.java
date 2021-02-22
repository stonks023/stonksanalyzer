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

            while ((output = br.readLine()) != null) {
                builder.append(output + System.lineSeparator());
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return builder.toString();
    }

    public static boolean storeCompanyData(String company, String data) {
        // TODO: Don't need to have folder for date
        if (dataExistsForCompany(company)) {
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
