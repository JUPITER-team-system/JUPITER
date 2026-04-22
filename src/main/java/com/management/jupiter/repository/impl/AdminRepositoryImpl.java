package com.management.jupiter.repository.impl;

import com.management.jupiter.models.*;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.UserRepository;
import com.management.jupiter.security.PasswordHasher;

import java.sql.*;
import java.util.*;

public class AdminRepositoryImpl implements UserRepository {

    // ─── SAVE ─────────────────────────────────────────────────────────────────
    @Override
    public Void save(User user) {
        String sql = """
                INSERT INTO "Cohorte"."user"
                    (full_name, email, password, role, specialty, clan_id)
                VALUES (?, ?, ?, ?, ?, ?::uuid)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, PasswordHasher.hash(user.getPassword()));
            ps.setString(4, user.getRole().name());
            if (user instanceof Tl tl && tl.getTlType() != null)
                ps.setString(5, tl.getTlType().name());
            else
                ps.setNull(5, Types.VARCHAR);
            if (user.getClan_id() != null)
                ps.setString(6, user.getClan_id().getId());
            else
                ps.setNull(6, Types.OTHER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
        return null;
    }

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM \"Cohorte\".\"user\" ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapResultSetToUser(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error getting users: " + e.getMessage(), e);
        }
        return users;
    }

    // ─── FIND BY ID ───────────────────────────────────────────────────────────
    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM \"Cohorte\".\"user\" WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ─── FIND BY EMAIL ────────────────────────────────────────────────────────
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM \"Cohorte\".\"user\" WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @Override
    public void update(User user) {
        String sql = """
                UPDATE "Cohorte"."user"
                SET full_name = ?, email = ?, password = ?
                WHERE id = ?::uuid
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, PasswordHasher.hash(user.getPassword()));
            ps.setString(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @Override
    public void delete(String id) {
        String sql = "DELETE FROM \"Cohorte\".\"user\" WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    // ─── INSERT CSV ───────────────────────────────────────────────────────────
    @Override
    public void insertCSV(List<String[]> data) {
        String sql = """
                INSERT INTO "Cohorte"."user" (email, password, full_name, role, clan_id)
                VALUES (?, ?, ?, ?, NULL)
                ON CONFLICT (email) DO NOTHING
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (String[] row : data) {
                ps.setString(1, row[1]);
                ps.setString(2, PasswordHasher.hash(row[2]));
                ps.setString(3, row[3]);
                ps.setString(4, row[4]);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[DB] Error in bulk insert: " + e.getMessage());
            throw new RuntimeException("Bulk insert failed: " + e.getMessage(), e);
        }
    }

    /**
     * Carga masiva de Coders desde CSV y los asigna directamente a un clan.
     * Formato CSV esperado: full_name,email,password  (una fila = un coder)
     * Ignora duplicados de email (ON CONFLICT DO NOTHING).
     *
     * @param rows   Lista de filas [full_name, email, password]
     * @param clanId UUID del clan destino
     * @return número de coders insertados
     */
    public int bulkInsertCoders(List<String[]> rows, String clanId) {
        String sql = """
                INSERT INTO "Cohorte"."user"
                    (full_name, email, password, role, clan_id)
                VALUES (?, ?, ?, 'CODER', ?::uuid)
                ON CONFLICT (email) DO NOTHING
                """;
        int inserted = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (String[] row : rows) {
                if (row.length < 2) continue;          // mínimo nombre + email
                String name  = row[0].trim();
                String email = row[1].trim();
                String pass  = row.length >= 3 ? row[2].trim() : "Jupiter2024!";
                if (name.isEmpty() || email.isEmpty()) continue;
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, PasswordHasher.hash(pass));
                ps.setString(4, clanId);
                ps.addBatch();
                inserted++;
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Bulk coder insert failed: " + e.getMessage(), e);
        }
        return inserted;
    }

    // ─── UPDATE FIELD ─────────────────────────────────────────────────────────
    public void updateField(String idOrEmail, String newValue, String fieldName) {
        boolean isUuid = idOrEmail != null && idOrEmail.contains("-");
        String whereClause = isUuid ? "id = ?::uuid" : "email = ?";

        String safeField = switch (fieldName) {
            case "full_name" -> "full_name";
            case "email"     -> "email";
            case "password"  -> "password";
            case "specialty" -> "specialty";
            case "clan_id"   -> "clan_id";
            default -> throw new IllegalArgumentException("Field not allowed: " + fieldName);
        };

        String value = "password".equals(fieldName) ? PasswordHasher.hash(newValue) : newValue;

        String sql;
        if ("clan_id".equals(fieldName)) {
            // clan_id necesita cast uuid o NULL
            sql = "UPDATE \"Cohorte\".\"user\" SET clan_id = "
                    + (value == null || value.isBlank() ? "NULL" : "?::uuid")
                    + " WHERE " + whereClause;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                int idx = 1;
                if (value != null && !value.isBlank()) ps.setString(idx++, value);
                ps.setString(idx, idOrEmail);
                ps.executeUpdate();
                return;
            } catch (SQLException e) {
                throw new RuntimeException("Error updating clan_id: " + e.getMessage(), e);
            }
        }

        sql = "UPDATE \"Cohorte\".\"user\" SET " + safeField + " = ? WHERE " + whereClause;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, idOrEmail);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating field: " + e.getMessage(), e);
        }
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String id       = rs.getString("id");
        String name     = rs.getString("full_name");
        String email    = rs.getString("email");
        String password = rs.getString("password");
        String roleStr  = rs.getString("role");
        Role role = Role.valueOf(roleStr.toUpperCase());

        return switch (role) {
            case ADMIN -> new Admin(id, name, email, password, role);
            case CODER -> new Coder(id, name, email, password, role);
            case TL -> {
                String spec = rs.getString("specialty");
                TlType tlType = (spec != null && !spec.isBlank())
                        ? TlType.valueOf(spec.toUpperCase())
                        : TlType.PROGRAMACION;
                yield new Tl(id, name, email, password, role, tlType);
            }
        };
    }
}
