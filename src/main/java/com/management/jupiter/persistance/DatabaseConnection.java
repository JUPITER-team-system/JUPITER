package com.management.jupiter.persistance;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    //Defino las credenciales.
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");
    private static final ThreadLocal<Connection>  threadConnection = new ThreadLocal<>();

    //Constructor privado para que nadie acceda a la db
    private DatabaseConnection () {}

    public static Connection getConnection() throws SQLException {

        if (threadConnection.get() != null && !threadConnection.get().isClosed()){

            return threadConnection.get();

        }

        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        threadConnection.set(conn);
        return conn;

    }

    public static void startTransaction () throws SQLException{

        if (threadConnection.get() == null || threadConnection.get().isClosed()){

            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            conn.setAutoCommit(false);
            threadConnection.set(conn);

        }

    }

    public static void commit () throws SQLException {

        Connection conn = threadConnection.get();

        if (conn != null) {

            conn.commit();
            closeConnection();

        }

    }

    public static void rollback () {

        try {

            Connection conn = threadConnection.get();

            if (conn != null) {

                conn.rollback();
                closeConnection();

            }

        }catch (SQLException err) {

            System.err.println("Error in Rollback: " + err.getMessage());

        }

    }

    public static void closeConnection () throws SQLException {

        Connection conn = threadConnection.get();

        if (conn != null) {

            conn.close();
            threadConnection.remove();

        }

    }
}

