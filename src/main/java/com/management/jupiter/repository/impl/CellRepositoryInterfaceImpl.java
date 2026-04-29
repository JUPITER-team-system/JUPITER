package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.CellRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CellRepositoryInterfaceImpl implements CellRepositoryInterface {
    //Gets database connection from DatabaseConnection
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public Void save(Cell cell) {
        String sql = "INSERT INTO \"Cohorte\".cells(name, clan_id) VALUES (?, ?)";
        try(PreparedStatement stmnt = getConnection().prepareStatement(sql)){
            stmnt.setString(1, cell.getName());
            stmnt.setObject(2, cell.getClanId());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List getAll() {
        return List.of();
    }


    @Override
    public void update(Cell cell) {

    }

    @Override
    public Optional<Cell> findById(String id) {
        return Optional.empty();
    }
}
