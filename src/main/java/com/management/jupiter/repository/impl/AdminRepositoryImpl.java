package com.management.jupiter.repository.impl;

import com.management.jupiter.models.User;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Admin;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.UserRepository;
import com.management.jupiter.models.enums.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepositoryImpl implements UserRepository {
    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    @Override
    public void save(User user) {
        //Miramos el codigo SQL que vamos a ejecutar
        String sql = "INSERT INTO \"Cohorte\".user(email, password, full_name, rol, clan_id) VALUES (?,?,?,?,?)";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getRole().toString());
            if (user.getClan_id() != null){
                stmt.setString(5, user.getClan_id() != null ? user.getClan_id().getId() : null);
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
        //Creo la Lista para poder retornarla en consola
        List<User> usersDB = new ArrayList<>();
        //Hago la consulta
        String sql = "SELECT * FROM \"Cohorte\".user";
        //Hago una declaracion y ejecuto la consulta
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                User user = mapResultSetToUser(rs);
                usersDB.add(user);
            }
        }catch (SQLException e){
            System.out.println("[ERROR]: error to get users" + e.getMessage());
            throw new RuntimeException(e);
        }
        return usersDB;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM \"Cohorte\".user WHERE id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error to search user by Id: " + e.getMessage());
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
    public void delete(String id) {
    }

    @Override
    public User findByEmailorId(String emailOrId) {
        // Try to parse as ID first - check if it looks like a UUID
        try {
            // If it contains hyphens, treat as UUID ID
            if (emailOrId.contains("-")) {
                Optional<User> userById = findById(emailOrId);
                return userById.orElse(null);
            } else {
                // Try parsing as email
                Optional<User> userByEmail = findByEmail(emailOrId);
                return userByEmail.orElse(null);
            }
        } catch (Exception e) {
            // If not a number, treat as email
            Optional<User> userByEmail = findByEmail(emailOrId);
            return userByEmail.orElse(null);
        }
    }


    @Override
    public Optional<User> findByEmail(String email) {
        // Consulta SQL para buscar usuario por email
        String sql = "SELECT * FROM \"Cohorte\".user WHERE email = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            // Si encontramos un resultado, mapear a objeto User
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error to search user by email: " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        // Si no encontramos el usuario, retornar Optional vacío
        return Optional.empty();
    }
    

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        // Extraer datos del ResultSet Toma los objetos y los vuelve Strings
        String id = rs.getString("id");
        String username = rs.getString("full_name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        
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





}
