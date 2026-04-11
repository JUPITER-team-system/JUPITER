package com.management.jupiter.persistance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Handler {

    private final String basePath = "./data";

    public Handler() {
        File dir = new File(basePath);
        if (!dir.exists()) dir.mkdirs();
    }

    // Leer CSV ignorando la primera línea (header)
    public List<String[]> read(String fileName) {
        List<String[]> dataList = new ArrayList<>();
        File file = new File(basePath, fileName);

        try {
            // Crear archivo si no existe
            if (!file.exists()) file.createNewFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false; // Ignora header
                        continue;
                    }
                    if (!line.trim().isEmpty()) {
                        String[] data = line.split(",");
                        dataList.add(data);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    // Escribir CSV preservando header
    public void write(String fileName, List<String[]> data) {
        File file = new File(basePath, fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            // Agregar header si el archivo está vacío
            if (file.length() == 0) {
                if (fileName.equals("info.csv")) {
                    bw.write("name,email,password,role");
                } else if (fileName.equals("clans.csv")) {
                    bw.write("id,clanName,teamLeader,members");
                }
                bw.newLine();
            }

            // Escribir los datos
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}