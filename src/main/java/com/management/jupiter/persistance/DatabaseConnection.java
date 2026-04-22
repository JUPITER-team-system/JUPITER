package com.management.jupiter.persistance;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestión centralizada de la conexión a PostgreSQL (Supabase).
 *
 * DISEÑO INTENCIONAL — conexión por llamada:
 *   Supabase PgBouncer (puerto 6543) ya actúa como pool externo.
 *   Cada llamada abre y cierra su propia conexión; el pooler la reutiliza.
 *   Esto elimina el ThreadLocal que causaba:
 *     - Conexiones obsoletas que nunca se cerraban en el hilo JavaFX.
 *     - Datos cacheados (la misma conexión veía una snapshot antigua).
 *     - Lentitud acumulada por intentar reusar una conexión caída.
 *
 *  startTransaction / commit / rollback siguen disponibles para
 *  operaciones multi-paso que necesiten atomicidad.
 */
public class DatabaseConnection {

    private static final String DEFAULT_URL  = "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:6543/postgres?sslmode=require";
    private static final String DEFAULT_USER = "postgres.acoeqoairlzrjldgdizj";
    private static final String DEFAULT_PASS = "Jupiter1021923969";

    private static final String URL;
    private static final String USER;
    private static final String PASS;

    static {
        String url = DEFAULT_URL, user = DEFAULT_USER, pass = DEFAULT_PASS;
        try {
            Dotenv d = Dotenv.configure().ignoreIfMissing().load();
            String eu = d.get("DB_URL"), eU = d.get("DB_USER"), eP = d.get("DB_PASS");
            if (eu != null && !eu.isBlank()) url  = eu;
            if (eU != null && !eU.isBlank()) user = eU;
            if (eP != null && !eP.isBlank()) pass = eP;
        } catch (Exception ignored) {}
        URL = url; USER = user; PASS = pass;
    }

    // ThreadLocal solo para transacciones explícitas (ClanService.delete, etc.)
    private static final ThreadLocal<Connection> txConnection = new ThreadLocal<>();

    /** Abre una conexión fresca. Úsala siempre en try-with-resources. */
    public static Connection getConnection() throws SQLException {
        Connection tx = txConnection.get();
        if (tx != null && !tx.isClosed()) return tx;   // dentro de una transacción activa
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Inicia una transacción explícita en el hilo actual. */
    public static void startTransaction() throws SQLException {
        if (txConnection.get() == null || txConnection.get().isClosed()) {
            Connection c = DriverManager.getConnection(URL, USER, PASS);
            c.setAutoCommit(false);
            txConnection.set(c);
        }
    }

    public static void commit() throws SQLException {
        Connection c = txConnection.get();
        if (c != null) { c.commit(); c.close(); txConnection.remove(); }
    }

    public static void rollback() {
        try {
            Connection c = txConnection.get();
            if (c != null) { c.rollback(); c.close(); txConnection.remove(); }
        } catch (SQLException e) {
            System.err.println("[DB] rollback error: " + e.getMessage());
        }
    }

    public static void closeConnection() throws SQLException {
        Connection c = txConnection.get();
        if (c != null) { c.close(); txConnection.remove(); }
    }
}
