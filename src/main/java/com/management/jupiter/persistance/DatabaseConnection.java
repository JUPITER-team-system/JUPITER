package com.management.jupiter.persistance;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    //Defino las credenciales.
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");
    private static Connection connection = null;

    //Constructor privado para que nadie acceda a la db

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try{
                //Puente
                connection = DriverManager.getConnection(URL,USER, PASS);
                System.out.println("connected to the database");
            }catch (SQLException s){
                System.out.println("[Error]: It was not possible to connect to the database:" + s.getMessage());
            }

        }
        return connection;
    }
}

