package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Cell;
import com.management.jupiter.models.Coder;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CodeRepositoryImpl implements Repository<Coder, UUID> {
    @Override
    public UUID save(Coder coder) {
        return null;
    }

    @Override
    public List<Coder> getAll() {
        //Voy a la databse y realizo la consulta
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement("SELECT * FROM clans WHERE id = ? = clan_id");
        ) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Coder> findById(String id) {
        return Optional.empty();
    }

    @Override
    public void update(Coder coder) {

    }

    public List<Coder> getAllByClan(String idClan) {
        List<Coder> listCoders = new ArrayList<>();
        // Solo seleccionamos las columnas que necesitamos
        String sql = "SELECT username, email FROM users WHERE clan_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, idClan);

            if (idClan == null) {
                throw new IllegalArgumentException("The user dont have a clan");
            }

            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {

                    String name = rs.getString("username");
                    String email = rs.getString("email");


                    Coder objCoder = new Coder(name, email);

                    // 3. Lo añadimos a la lista
                    listCoders.add(objCoder);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error to get partners of clan: " + e.getMessage());
        }

        return listCoders;
    }
    public List<Cell> getAllCells() {
        List<Cell> cellList = new ArrayList<>();
        String sql = "SELECT id, name, clan_id FROM cells"; // Ajusta a tus nombres reales

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            while (rs.next()) {
                // 1. Extraemos los datos
                int id = rs.getInt("id");
                String name = rs.getString("name");

                UUID clanUuid = UUID.fromString(rs.getString("clan_id"));


                Cell cell = new Cell(name, clanUuid);


                cellList.add(cell);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar células: " + e.getMessage());
        }

        return cellList;
    }

    public List<Cell> getAllWithMembers() {
        // Usamos un Map para no duplicar Células si tienen varios miembros
        Map<Integer, Cell> cellMap = new HashMap<>();

        String sql = """
        SELECT c.id AS cell_id, c.name AS cell_name, c.clan_id,u.username, u.email FROM cells cLEFT JOIN users u ON c.id = u.cell_id""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            while (rs.next()) {
                int cellId = rs.getInt("cell_id");

                // Si la célula aún no está en el mapa, la creamos
                Cell cell = cellMap.get(cellId);
                if (cell == null) {
                    cell = new Cell(rs.getString("cell_name"), UUID.fromString(rs.getString("clan_id")));
                    cellMap.put(cellId, cell);
                }

                // Si hay un coder en esta fila (por el LEFT JOIN), lo añadimos a la lista final de la célula
                String coderName = rs.getString("username");
                if (coderName != null) {
                    Coder coder = new Coder(coderName, rs.getString("email"));
                    cell.getMembers().add(coder); // .getMembers() devuelve la lista final, podemos hacer .add()
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(cellMap.values());
    }

}
