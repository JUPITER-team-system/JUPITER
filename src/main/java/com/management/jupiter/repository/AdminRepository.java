package com.management.jupiter.repository;
import com.management.jupiter.persistance.Handler;
import java.io.*;
import java.util.List;
import com.management.jupiter.persistance.DatabaseConnection;

import static com.management.jupiter.persistance.ReadCSV.readCSV;


public class AdminRepository {
    private Handler handler;
    private DatabaseConnection DB;

    public AdminRepository() {
        handler = new Handler();
    }

    //Hacemos un receptor de archivos
    public void importerCSV(InputStream inputStream){
        new Thread(()->{
            try {
                List<String[]> filas = readCSV(inputStream);
               insertLot(filas);

               handler.post(() ->{
                   System.out.println("Import Agree!!");
               });
            }catch (Exception e){
                e.printStackTrace();
                handler.post(()->{
                    System.out.println("Import Error");
                });
            }
        }).start();
    }


}