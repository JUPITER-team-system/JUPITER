package com.management.jupiter.repository;

import com.management.jupiter.models.Coder;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.persistance.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio en memoria para Coders.
 * Necesario para que AssignmentService pueda buscar Coders por ID (US-04).
 */
public class CoderRepository {

    private final Map<String, Coder> coders = new HashMap<>();

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // ── CREATE / UPDATE ──────────────────────────────────────────────────────

    public void save(Coder coder) {
        coders.put(coder.getId(), coder);
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    public List<Coder> findAll() {
        return new ArrayList<>(coders.values());
    }

    public Coder findById(String id) {
        return coders.get(id);
    }

    public Optional<Coder> findByEmailOrId(String emailOrId) {
        String sql = """
                SELECT id, full_name, email, password, role
                FROM "Cohorte"."user"
                WHERE UPPER(role) = ? AND (email = ? OR id = ?)
                """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, Role.CODER.name());
            stmt.setString(2, emailOrId);

            try {
                stmt.setObject(3, UUID.fromString(emailOrId));
            } catch (IllegalArgumentException e) {
                stmt.setObject(3, null);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Coder(
                            rs.getString("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            Role.CODER
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Optional<UUID> findCoderClanId(String coderId) {
        String sql = """
                SELECT clan_id
                FROM "Cohorte"."user"
                WHERE id = ? AND UPPER(role) = ?
                """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(coderId));
            stmt.setString(2, Role.CODER.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable((UUID) rs.getObject("clan_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public void assignCoderToClan(String coderId, UUID clanId) {
        String sql = """
                UPDATE "Cohorte"."user"
                SET clan_id = ?, cell_id = NULL
                WHERE id = ? AND UPPER(role) = ?
                """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, clanId);
            stmt.setObject(2, UUID.fromString(coderId));
            stmt.setString(3, Role.CODER.name());

            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Coder was not assigned to the clan.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unassignCoderFromClan(String coderId, UUID clanId) {
        String sql = """
                UPDATE "Cohorte"."user"
                SET clan_id = NULL, cell_id = NULL
                WHERE id = ? AND clan_id = ? AND UPPER(role) = ?
                """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(coderId));
            stmt.setObject(2, clanId);
            stmt.setString(3, Role.CODER.name());

            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Coder was not assigned to this clan.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void delete(String id) {
        coders.remove(id);
    }
}
