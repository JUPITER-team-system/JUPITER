package com.management.jupiter.impl;

import com.management.jupiter.models.User;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.UserRepository;

import java.sql.*;

public abstract class UserRepositoryImpl implements UserRepository {
    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    @Override
    public void save(User user) {
        //Miramos el codigo SQL que vamos a ejecutar
        String sql = "INSERT INTO users(Email, password, all_name, rol, clan_id) VALUES (?,?,?,?,?)";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getRole());
            if ()
        }


    }
}
