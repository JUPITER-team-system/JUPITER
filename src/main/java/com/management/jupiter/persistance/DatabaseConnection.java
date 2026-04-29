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
        // Disable server-side prepared statements to avoid naming conflicts
        try {
            conn.prepareStatement("SET prepareThreshold = 0").executeUpdate();
        } catch (SQLException e) {
            // If prepareThreshold is not supported, try alternative approach
            try {
                conn.prepareStatement("SET server_side_prep = off").executeUpdate();
            } catch (SQLException e2) {
                // If neither works, continue without disabling prepared statements
                System.err.println("Warning: Could not disable prepared statements: " + e2.getMessage());
            }
        }
        threadConnection.set(conn);
        return conn;

    }

    public static void startTransaction () throws SQLException{

        if (threadConnection.get() == null || threadConnection.get().isClosed()){

            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            conn.setAutoCommit(false);
            // Disable server-side prepared statements to avoid naming conflicts
            try {
                conn.prepareStatement("SET prepareThreshold = 0").executeUpdate();
            } catch (SQLException e) {
                // If prepareThreshold is not supported, try alternative approach
                try {
                    conn.prepareStatement("SET server_side_prep = off").executeUpdate();
                } catch (SQLException e2) {
                    // If neither works, continue without disabling prepared statements
                    System.err.println("Warning: Could not disable prepared statements: " + e2.getMessage());
                }
            }
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

