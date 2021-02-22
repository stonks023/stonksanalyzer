package com.stonks.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    public static List<String> readFile(String fileLocation) {
        List<String> companies = new ArrayList<>();
        try {
            File myObj = new File(fileLocation);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                companies.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return companies;
    }

    public static String readExistingFile(String fileLocation) {
        StringBuilder builder = new StringBuilder();

        try {
            File myObj = new File(fileLocation);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                builder.append(data + System.lineSeparator());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return builder.toString();
    }
}
