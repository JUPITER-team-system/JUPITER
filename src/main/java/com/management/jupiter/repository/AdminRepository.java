package com.management.jupiter.repository;
import com.management.jupiter.persistance.Handler;
import java.io.*;
import java.util.List;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;

import static com.management.jupiter.persistance.ReadCSV.readCSV;


public class AdminRepository {
    private Handler handler;
    private DatabaseConnection DB;
    private AdminRepositoryImpl adminRepositoryImpl;

    public AdminRepository() {
        handler = new Handler();
        adminRepositoryImpl = new AdminRepositoryImpl();
    }

    public void insertCSV(List<String[]> data) {
        adminRepositoryImpl.insertCSV(data);
    }

    //Hacemos un receptor de archivos
    public void importerCSV(InputStream inputStream){
        new Thread(()->{
            try {
                List<String[]> filas = readCSV(inputStream);
               insertCSV(filas);

               System.out.println("Import Agree!!");
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Import Error");
            }
        }).start();
    }


}