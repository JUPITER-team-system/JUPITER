package com.management.jupiter.repository.impl;

import com.management.jupiter.models.User;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Clan;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.UserRepository;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.security.PasswordHasher;

import java.sql.*;
import java.util.*;

public class AdminRepositoryImpl implements UserRepository {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public Void save(User user) {
        String sql = "INSERT INTO \"Cohorte\".user(email, password, full_name, role, clan_id, specialty) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, PasswordHasher.hash(user.getPassword()));
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getRole().toString());
            if (user.getClan_id() != null && user.getClan_id().getId() != null) {
                try {
                    stmt.setObject(5, UUID.fromString(user.getClan_id().getId()));
                } catch (IllegalArgumentException e) {
                    stmt.setNull(5, Types.OTHER);
                }
            } else {
                stmt.setNull(5, Types.OTHER);
            }
            if (user instanceof Tl tl) {
                stmt.setString(6, tl.getTlType() != null ? tl.getTlType().name() : TlType.PROGRAMACION.name());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            stmt.executeUpdate();
            System.out.println("User saved successfully");
        } catch (SQLException e) {
            System.out.println("[ERROR]: Could not insert user: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        List<User> usersDB = new ArrayList<>();
        String sql = "SELECT * FROM \"Cohorte\".user";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usersDB.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error getting users: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return usersDB;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        String sql = "SELECT * FROM \"Cohorte\".user WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            try {
                stmt.setObject(1, UUID.fromString(id));
            } catch (IllegalArgumentException e) {
                stmt.setString(1, id);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapResultSetToUser(rs));
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error searching user by id: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE \"Cohorte\".user SET email = ?, full_name = ?, role = ?, clan_id = ? WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getRole().toString());
            if (user.getClan_id() != null && user.getClan_id().getId() != null) {
                try {
                    stmt.setObject(4, UUID.fromString(user.getClan_id().getId()));
                } catch (IllegalArgumentException e) {
                    stmt.setNull(4, Types.OTHER);
                }
            } else {
                stmt.setNull(4, Types.OTHER);
            }
            try {
                stmt.setObject(5, UUID.fromString(user.getId()));
            } catch (IllegalArgumentException e) {
                stmt.setString(5, user.getId());
            }
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "User updated successfully" : "[WARN]: User not found: " + user.getId());
        } catch (Exception e) {
            System.out.println("[ERROR]: Error updating user: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM \"Cohorte\".user WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            try {
                stmt.setObject(1, UUID.fromString(id));
            } catch (IllegalArgumentException e) {
                stmt.setString(1, id);
            }
            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "User deleted: " + id : "[WARN]: User not found: " + id);
        } catch (SQLException e) {
            System.out.println("[ERROR]: Error deleting user: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertCSV(List<String[]> data) {
        String sql = "INSERT INTO \"Cohorte\".user(email, password, full_name, role, clan_id) VALUES (?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
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
        } catch (SQLException e) {
            System.err.println("[ERROR]: Bulk upload failed: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM \"Cohorte\".user WHERE email = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapResultSetToUser(rs));
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

        Clan clan = loadClanFromRow(rs);

        switch (role) {
            case ADMIN:
                return new Admin(id, username, email, password, role);
            case CODER:
                // Always return a proper Coder instance so casting to Coder works on login
                return new Coder(id, username, email, password, role, clan);
            case TL:
                Tl tl = new Tl(id, username, email, password, role, mapTlType(rs));
                if (clan != null) {
                    tl.addClan(clan);
                }
                return tl;
            default:
                return new User(id, username, email, password, role, clan);
        }
    }

    private Clan loadClanFromRow(ResultSet rs) throws SQLException {
        String clanIdStr;
        try {
            clanIdStr = rs.getString("clan_id");
        } catch (SQLException ignored) {
            return null;
        }
        if (clanIdStr == null || clanIdStr.isBlank()) return null;

        String sql = """
                SELECT id, name, description FROM "Cohorte".clan WHERE id = ?
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(clanIdStr));
            try (ResultSet clanRs = stmt.executeQuery()) {
                if (clanRs.next()) {
                    return new Clan(clanRs.getString("id"),
                                   clanRs.getString("name"),
                                   clanRs.getString("description"));
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[WARN]: Invalid clan_id UUID: " + clanIdStr);
        }
        return null;
    }

    private TlType mapTlType(ResultSet rs) {
        try {
            String specialty = rs.getString("specialty");
            if (specialty != null && !specialty.isBlank()) {
                return TlType.valueOf(specialty.trim().toUpperCase());
            }
        } catch (SQLException | IllegalArgumentException ignored) {}
        return TlType.PROGRAMACION;
    }
}
