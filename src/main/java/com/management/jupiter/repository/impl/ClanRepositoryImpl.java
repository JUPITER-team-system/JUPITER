package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.ClanRepository;

import java.sql.*;
import java.util.*;

/**
 * Repositorio de Clanes.
 *
 * ESTRUCTURA REAL DE LA BD (Supabase / Cohorte):
 *   clan        : id (uuid), name, description, created_at
 *   user        : id, full_name, email, password, role, specialty, clan_id (uuid FK→clan), cell_id
 *   cells       : id (uuid), name, created_at, clan_id (uuid FK→clan)
 *   information : id (uuid), title, message, clan_id (uuid FK→clan), created_at
 *
 * NO EXISTE tabla clan_members — la relación usuario↔clan se gestiona
 * mediante el campo user.clan_id.
 */
public class ClanRepositoryImpl implements ClanRepository {

    // ═══════════════════════════════════════════════════════
    //  CLAN CRUD
    // ═══════════════════════════════════════════════════════

    @Override
    public List<Clan> getAll() {
        Map<String, Clan> clanMap = new LinkedHashMap<>();

        // Traer clanes con sus miembros via user.clan_id
        String sql = """
                SELECT
                    c.id          AS clan_id,
                    c.name        AS clan_name,
                    c.description AS clan_description,
                    u.id          AS user_id,
                    u.full_name   AS user_name,
                    u.email       AS user_email,
                    u.specialty   AS user_specialty,
                    u.role        AS user_role
                FROM "Cohorte".clan c
                LEFT JOIN "Cohorte"."user" u ON u.clan_id = c.id
                ORDER BY c.name
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String clanId = rs.getString("clan_id");
                Clan clan = clanMap.computeIfAbsent(clanId, id -> {
                    try {
                        return new Clan(id,
                                rs.getString("clan_name"),
                                rs.getString("clan_description"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                String userId = rs.getString("user_id");
                if (userId != null) {
                    mapUserToClan(clan, rs, userId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error to get clans: " + e.getMessage(), e);
        }

        return new ArrayList<>(clanMap.values());
    }

    @Override
    public Optional<Clan> findById(String id) {
        return findByIdOrName(id);
    }

    @Override
    public Optional<Clan> findByIdOrName(String value) {
        Map<String, Clan> clanMap = new LinkedHashMap<>();

        String sql = """
                SELECT
                    c.id          AS clan_id,
                    c.name        AS clan_name,
                    c.description AS clan_description,
                    u.id          AS user_id,
                    u.full_name   AS user_name,
                    u.email       AS user_email,
                    u.specialty   AS user_specialty,
                    u.role        AS user_role
                FROM "Cohorte".clan c
                LEFT JOIN "Cohorte"."user" u ON u.clan_id = c.id
                WHERE c.name = ? OR c.id::text = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String clanId = rs.getString("clan_id");
                    Clan clan = clanMap.computeIfAbsent(clanId, id -> {
                        try {
                            return new Clan(id,
                                    rs.getString("clan_name"),
                                    rs.getString("clan_description"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    String userId = rs.getString("user_id");
                    if (userId != null) mapUserToClan(clan, rs, userId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error to get clan: " + e.getMessage(), e);
        }

        return clanMap.values().stream().findFirst();
    }

    @Override
    public UUID save(Clan clan) {
        String sql = "INSERT INTO \"Cohorte\".clan (name, description) VALUES (?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clan.getName());
            ps.setString(2, clan.getDescription() != null ? clan.getDescription() : "");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return (UUID) rs.getObject(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving clan: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void update(Clan clan) {
        String sql = "UPDATE \"Cohorte\".clan SET name = ?, description = ? WHERE id = ?::uuid";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clan.getName());
            ps.setString(2, clan.getDescription() != null ? clan.getDescription() : "");
            ps.setString(3, clan.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating clan: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        // Desasignar usuarios del clan antes de eliminarlo
        String detachUsers = "UPDATE \"Cohorte\".\"user\" SET clan_id = NULL WHERE clan_id = ?::uuid";
        String deleteCells = "DELETE FROM \"Cohorte\".cells WHERE clan_id = ?::uuid";
        String deleteInfo  = "DELETE FROM \"Cohorte\".information WHERE clan_id = ?::uuid";
        String deleteClan  = "DELETE FROM \"Cohorte\".clan WHERE id = ?::uuid";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (String sql : new String[]{detachUsers, deleteCells, deleteInfo, deleteClan}) {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, id);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting clan: " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════════════
    //  ASIGNACIÓN DE USUARIOS AL CLAN  (via user.clan_id)
    // ═══════════════════════════════════════════════════════

    /** Asigna un usuario a un clan actualizando user.clan_id. */
    @Override
    public void addUser(UUID clanId, String userId) throws SQLException {
        String sql = "UPDATE \"Cohorte\".\"user\" SET clan_id = ? WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, clanId);
            ps.setString(2, userId);
            ps.executeUpdate();
        }
    }

    /** Desasigna todos los usuarios de un clan (clan_id → NULL). */
    @Override
    public void removeUser(String clanId) throws SQLException {
        String sql = "UPDATE \"Cohorte\".\"user\" SET clan_id = NULL WHERE clan_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clanId);
            ps.executeUpdate();
        }
    }

    // ═══════════════════════════════════════════════════════
    //  MESSAGES (tabla information)
    // ═══════════════════════════════════════════════════════

    /** Devuelve todos los mensajes de un clan. */
    public List<String[]> getMessagesByClan(String clanId) {
        List<String[]> messages = new ArrayList<>();
        String sql = """
                SELECT id, title, message, created_at
                FROM "Cohorte".information
                WHERE clan_id = ?::uuid
                ORDER BY created_at DESC
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(new String[]{
                            rs.getString("id"),
                            rs.getString("title"),
                            rs.getString("message"),
                            rs.getString("created_at")
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading messages: " + e.getMessage(), e);
        }
        return messages;
    }

    /** Crea un nuevo mensaje para un clan. */
    public String saveMessage(String clanId, String title, String message) {
        String sql = """
                INSERT INTO "Cohorte".information (title, message, clan_id)
                VALUES (?, ?, ?::uuid) RETURNING id
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, message);
            ps.setString(3, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving message: " + e.getMessage(), e);
        }
        return null;
    }

    /** Elimina un mensaje por su ID. */
    public void deleteMessage(String messageId) {
        String sql = "DELETE FROM \"Cohorte\".information WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, messageId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting message: " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════════════
    //  CELLS (tabla cells)
    // ═══════════════════════════════════════════════════════

    /** Devuelve todas las cells de un clan. */
    public List<String[]> getCellsByClan(String clanId) {
        List<String[]> cells = new ArrayList<>();
        String sql = """
                SELECT id, name, created_at
                FROM "Cohorte".cells
                WHERE clan_id = ?::uuid
                ORDER BY name
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cells.add(new String[]{
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("created_at")
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading cells: " + e.getMessage(), e);
        }
        return cells;
    }

    /** Crea una nueva cell para un clan. */
    public String saveCell(String clanId, String name) {
        String sql = """
                INSERT INTO "Cohorte".cells (name, clan_id)
                VALUES (?, ?::uuid) RETURNING id
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving cell: " + e.getMessage(), e);
        }
        return null;
    }

    /** Elimina una cell por su ID. */
    public void deleteCell(String cellId) {
        String sql = "DELETE FROM \"Cohorte\".cells WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cellId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting cell: " + e.getMessage(), e);
        }
    }

    // ═══════════════════════════════════════════════════════
    //  HELPER PRIVADO
    // ═══════════════════════════════════════════════════════

    private void mapUserToClan(Clan clan, ResultSet rs, String userId) throws SQLException {
        String name    = rs.getString("user_name");
        String email   = rs.getString("user_email");
        String roleStr = rs.getString("user_role");
        if (roleStr == null) return;

        Role role = Role.valueOf(roleStr.toUpperCase());

        if (role == Role.TL) {
            String specialtyRaw = rs.getString("user_specialty");
            TlType tlType = (specialtyRaw != null && !specialtyRaw.isBlank())
                    ? TlType.valueOf(specialtyRaw.toUpperCase())
                    : TlType.PROGRAMACION;
            clan.getTls().add(new Tl(userId, name, email, null, role, tlType));
        } else if (role == Role.CODER) {
            clan.getCoders().add(new Coder(userId, name, email, null, role));
        }
        // ADMIN no se agrega a la lista de miembros del clan
    }

    /**
     * Devuelve mensajes de un clan con nombre del autor (user que los publicó).
     * La tabla information tiene clan_id pero no user_id, así que mostramos
     * el título como identificador del TL que lo publicó (hasta que la BD tenga
     * la columna author_id).
     * Retorna: [id, title, message, created_at]  — misma firma que getMessagesByClan
     * pero ordenado DESC por created_at.
     */
    public List<String[]> getMessagesByClanForCoder(String clanId) {
        return getMessagesByClan(clanId);  // ya viene DESC
    }

}
