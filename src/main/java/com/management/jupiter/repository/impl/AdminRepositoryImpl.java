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

/**
 * Implementation of UserRepository for PostgreSQL database operations
 * Handles all CRUD operations for User entities
 */
public class AdminRepositoryImpl implements UserRepository {
    
    /**
     * Gets database connection from DatabaseConnection
     * @return Database connection
     * @throws SQLException if connection fails
     */
    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    /**
     * Saves a new user to the database
     * @param user User object to save
     */
    @Override
    public void save(User user) {
        String sql = "INSERT INTO \"Cohorte\".user(email, password, full_name, role, clan_id) VALUES (?,?,?,?,?)";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getRole().toString());
            if (user.getClan_id() != null){
                stmt.setString(5, user.getClan_id().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.executeUpdate();
            System.out.println("User saved successfully");
        } catch (SQLException e) {
            System.out.println("[ERROR]: It was not possible to insert the user");
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all users from database
     * @return List of all users
     */
    @Override
    public List<User> getAll() {
        List<User> usersDB = new ArrayList<>();
        String sql = "SELECT * FROM \"Cohorte\".user";
        
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                User user = mapResultSetToUser(rs);
                usersDB.add(user);
            }
        }catch (SQLException e){
            System.out.println("[ERROR]: Error getting users: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return usersDB;
    }

    /**
     * Finds user by long ID (not implemented)
     * @param id User's long ID
     * @return Empty optional (not implemented)
     */
    @Override
    public Optional<User> findById(long id) {
        return Optional.empty();
    }

    /**
     * Finds user by string ID
     * @param id User's string ID
     * @return Optional containing user if found
     */
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

    /**
     * Updates user in database
     * @param user User object with updated data
     */
    @Override
    public void update(User user) {
        // Implementation already added above
    }

    /**
     * Deletes user by long ID (not implemented)
     * @param id User's long ID
     */
    @Override
    public void delete(long id) {
        // Not implemented - using String ID version
    }

    /**
     * Deletes user by string ID
     * @param id User's string ID
     */
    @Override
    public void delete(String id) {
        // Implementation already added above
    }

    

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM \"Cohorte\".user WHERE email = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error searching user by email: " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return Optional.empty();
    }
    

    /**
     * Maps ResultSet to User object with proper type casting
     * @param rs ResultSet from database query
     * @return User object (Admin, Coder, or TL based on role)
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String username = rs.getString("full_name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        
        Role role = Role.valueOf(roleStr.toUpperCase());
        
        switch (role) {
            case ADMIN:
                return new Admin(id, username, email, password, role);
            case CODER:
                return new Coder(id, username, email, password, role);
            case TL:
                return new Tl(id, username, email, password, role, null);
            default:
                return new User(id, username, email, password, role, null);
        }
    }





}
