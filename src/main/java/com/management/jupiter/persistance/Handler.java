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

    public static int nextId(String fileName) {
        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length == 0) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(data[0].trim());
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException ignored) {
                    // Compatibilidad con filas viejas sin ID al inicio.
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return maxId + 1;
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
                } else if (fileName.equals("cells.csv")) {
                    bw.write("id,cellName");
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