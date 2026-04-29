package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Coder;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CodeRepositoryImpl implements Repository<Coder, UUID> {
    @Override
    public UUID save(Coder coder) {
        return null;
    }

    @Override
    public List<Coder> getAll() {
       //Voy a la databse y realizo la consulta
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM clans WHERE id = ? == clan_id");
        ){

        } catch (Exception e){
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

    public List<Coder> getAllByClan(int idClan) {
        List<Coder> listCoders = new ArrayList<>();
        // Solo seleccionamos las columnas que necesitamos
        String sql = "SELECT username, email FROM users WHERE clan_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, idClan);

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
            System.err.println("Error al obtener compañeros del clan: " + e.getMessage());
        }

        return listCoders;
    }
}
