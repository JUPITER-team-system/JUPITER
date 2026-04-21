package com.management.jupiter.repository.impl;

import com.management.jupiter.models.User;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Admin;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.UserRepository;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.security.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.management.jupiter.persistance.DatabaseConnection.getConnection;

public class AdminRepositoryImpl implements UserRepository {
    //Gets database connection from DatabaseConnection
    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }


     //*Saves a new user to the database

    @Override
    public Void save(User user) {
        String sql = "INSERT INTO \"Cohorte\".user(email, password, full_name, role, clan_id) VALUES (?,?,?,?,?)";
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, user.getEmail());
            //Hasheamos la password
            String hashedPassword = PasswordHasher.hash(user.getPassword());
            //Procedemos con insertarla
            stmt.setString(2, hashedPassword);
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

        return null;
    }


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

    // Finds user by string ID
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


    //Updates user in database
@Override
    public void update(User user) {
        // 1. Corregimos el SQL: Agregamos placeholders para 'role' y 'clan_id'
        // Y movemos el 'id' al final (posición 6)
        String sql = "UPDATE \"Cohorte\".user SET email = ?, password = ?, full_name = ?, role = ?, clan_id = ? WHERE id = ?";

        try(Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, user.getEmail());

            // ¡OJO! Asegúrate de que este hash no se haga dos veces si ya está hasheado.
            String hashed = PasswordHasher.hash(user.getPassword());
            stmt.setString(2, hashed);

            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getRole().toString());

            // Manejo de clan_id
            if (user.getClan_id() != null){
                stmt.setString(5, user.getClan_id().getId());
            } else {
                stmt.setNull(5, Types.OTHER);
            }

            // El ID va al final
            stmt.setString(6, user.getId());

            int rowsUpdate = stmt.executeUpdate();

            if (rowsUpdate > 0){
                System.out.println("The user has modified correct");
            } else {
                System.out.println("[ERROR]: No user found with id: " + user.getId());
            }
        } catch (Exception e) {
            System.out.println("[ERROR]: Error trying to update user: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }






// delete by ID
    @Override
    public void delete(String id)   {
        //Empezamos con la consulta SQL
        String sql = "DELETE FROM \"Cohorte\".user WHERE id = ?";
        //Cocinamos la declaracion SQL
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            //Pasamos a servirla y nos dice cuantos platos se recogieron
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0){
                System.out.printf("User delete success!!" + id);
            }else {
                System.out.printf("User not found with id" + id);
            }
        }catch (SQLException e){
            System.out.println("[ERROR]: error to try delete this user: " + e.getMessage());
            throw new RuntimeException();
        }

    }

    @Override
    public void insertCSV(List<String[]> data) {
        String sql = "INSERT INTO \"Cohorte\".user(email, password, full_name, role, clan_id) VALUES (?,?,?,?,?)";
        Connection conn = null; // 1. Declaramos fuera

        try {
            conn = getConnection(); // 2. Iniciamos la conexión
            conn.setAutoCommit(false); // Iniciamos transacción

            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Usamos try-with-resources solo para el Statement
                for (String[] row : data) {
                    ps.setString(1, row[1]);
                    ps.setString(2, row[2]);
                    ps.setString(3, row[3]);
                    ps.setString(4, row[4]);
                    ps.setNull(5, Types.OTHER);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                System.out.println("Bulk upload completed successfully");
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Error in bulk upload", e);
        } finally {
            // 4. Cerramos manualmente la conexión para no dejarla abierta
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    //Creo funcion de buscar usuarios con el Clan Nulo para poder asignarlos
    public List<User> findUsersWhitouClan(){
        List<User> userWhitouClan = new ArrayList<>();
        String SQL = "SELECT \"Cohorte\".user SET clan_id = ? WHERE id = ? AND clan_id IS NULL";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(SQL)){
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                User user = mapResultSetToUser(rs);
                userWhitouClan.add(user);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error searching users whitou clan" + e.getMessage());
            throw new RuntimeException(e);
        }
        return userWhitouClan;
    }

    public void assignClan(String userID, String clanId){
        String SQL = "UPDATE \"Cohorte\".user SET clan_id = ? WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(SQL)){
            stmt.setString(1, clanId);
            stmt.setString(2, userID);
        int rowsUpdate = stmt.executeUpdate();
            if (rowsUpdate > 0){
                System.out.println("Clan assigned to user: " + userID);
            }else{
                System.out.println("[ERROR]: Not user found with id: " + userID);
            }
            } catch (SQLException e) {
            System.out.println("[ERROR]: Error to asigment clan: " + e.getMessage());
            throw new RuntimeException(e);
        }
        }
    }


