package com.management.jupiter.impl;

import com.management.jupiter.models.User;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Admin;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.UserRepository;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    @Override
    public void save(User user) {
        //Miramos el codigo SQL que vamos a ejecutar
        String sql = "INSERT INTO Cohorte.users(email, password, all_name, rol, clan_id) VALUES (?,?,?,?,?)";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getRole().toString());
            if (user.getClan_id() != null){
                stmt.setLong(5, user.getClan_id().getId());
            }else {stmt.setNull(5, Types.BIGINT);}

            stmt.executeUpdate();
            System.out.println("User save's agree!!");
        } catch (SQLException e) {
            System.out.println("[ERROR]: It was not possible to insert the user");
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM Cohorte.users WHERE id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error al buscar usuario por ID: " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return Optional.empty();
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(long id) {
    }

    @Override
    public User findByEmailorId(String emailOrId) {
        // Try to parse as ID first
        try {
            long id = Long.parseLong(emailOrId);
            Optional<User> userById = findById(id);
            return userById.orElse(null);
        } catch (NumberFormatException e) {
            // If not a number, treat as email
            Optional<User> userByEmail = findByEmail(emailOrId);
            return userByEmail.orElse(null);
        }
    }


    @Override
    public Optional<User> findByEmail(String email) {
        // Consulta SQL para buscar usuario por email
        String sql = "SELECT * FROM \"Cohorte\".users WHERE email = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            // Si encontramos un resultado, mapear a objeto User
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error al buscar usuario por email: " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        // Si no encontramos el usuario, retornar Optional vacío
        return Optional.empty();
    }
    

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        // Extraer datos del ResultSet
        int id = rs.getInt("id");
        String username = rs.getString("all_name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String roleStr = rs.getString("rol");
        
        // Convertir string a enum Role
        Role role = Role.valueOf(roleStr.toUpperCase());
        
        // Crear objeto específico según el rol
        switch (role) {
            case ADMIN:
                return new Admin(id, username, email, password, role);
            case CODER:
                return new Coder(id, username, email, password, role);
            case TL:
                // Para TL, necesitamos TlType (temporalmente null hasta tenerlo en BD)
                return new Tl(id, username, email, password, role, null);
            default:
                return new User(id, username, email, password, role, null);
        }
    }
    // Los demás métodos los dejaremos vacíos por ahora para enfocarnos en el INSERT

}
