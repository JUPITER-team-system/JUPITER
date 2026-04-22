package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Cell;
import com.management.jupiter.models.User;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.CellRepositoryInterface;

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
    public void save(Cell cell) {
        String sql = "INSERT INTO \"Cohorte\".cells(name) VALUES (?)";
        try(PreparedStatement stmnt = getConnection().prepareStatement(sql)){
            stmnt.setString(1, cell.getName());
            stmnt.executeUpdate();
            System.out.println("Cell created");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List getAll() {
        return List.of();
    }

    @Override
    public Optional findById(long id) {
        return Optional.empty();
    }

    @Override
    public void update(Cell cell) {

    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void insertCSV(List<String[]> data) {

    }
}
