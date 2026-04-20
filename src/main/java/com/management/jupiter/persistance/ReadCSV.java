package com.management.jupiter.persistance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadCSV {
    public static List<String[]> readCSV(InputStream inputStream) throws IOException {
    List<String[]> data = new ArrayList<>();
    //Logica de lectura
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine())!= null){
            data.add(line.split(","));
        }
        return data;
    }
}
