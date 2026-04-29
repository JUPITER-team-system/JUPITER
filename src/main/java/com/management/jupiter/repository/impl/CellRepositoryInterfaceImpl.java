package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Cell;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.CellRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CellRepositoryInterfaceImpl implements CellRepositoryInterface {
    //Gets database connection from DatabaseConnection
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public Void save(Cell cell) {
        saveAndReturnId(cell);
        return null;
    }

    @Override
    public UUID saveAndReturnId(Cell cell) {
        String sql = "INSERT INTO \"Cohorte\".cells(name, clan_id) VALUES (?, ?) RETURNING id";
        try(PreparedStatement stmnt = getConnection().prepareStatement(sql)){
            stmnt.setString(1, cell.getName());
            stmnt.setObject(2, cell.getClanId());

            try (ResultSet rs = stmnt.executeQuery()) {
                if (rs.next()) {
                    return (UUID) rs.getObject("id");
                }
            }

            throw new SQLException("The cell was not created.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Coder> findCodersByClanId(UUID clanId) {
        List<Coder> coders = new ArrayList<>();
        String sql = """
                SELECT id, full_name, email, password, role
                FROM "Cohorte"."user"
                WHERE clan_id = ? AND UPPER(role) = ?
                """;

        try (PreparedStatement stmnt = getConnection().prepareStatement(sql)) {
            stmnt.setObject(1, clanId);
            stmnt.setString(2, Role.CODER.name());

            try (ResultSet rs = stmnt.executeQuery()) {
                while (rs.next()) {
                    coders.add(new Coder(
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

        return coders;
    }

    @Override
    public Map<String, List<Coder>> findCodersGroupedByCell(UUID clanId) {
        Map<String, List<Coder>> codersByCell = new LinkedHashMap<>();
        String sql = """
                SELECT
                    COALESCE(c.name, 'Without cell') AS cell_name,
                    u.id,
                    u.full_name,
                    u.email,
                    u.password
                FROM "Cohorte"."user" u
                LEFT JOIN "Cohorte".cells c ON c.id = u.cell_id
                WHERE u.clan_id = ? AND UPPER(u.role) = ?
                ORDER BY c.name NULLS LAST, u.full_name
                """;

        try (PreparedStatement stmnt = getConnection().prepareStatement(sql)) {
            stmnt.setObject(1, clanId);
            stmnt.setString(2, Role.CODER.name());

            try (ResultSet rs = stmnt.executeQuery()) {
                while (rs.next()) {
                    String cellName = rs.getString("cell_name");
                    Coder coder = new Coder(
                            rs.getString("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            Role.CODER
                    );

                    codersByCell.computeIfAbsent(cellName, key -> new ArrayList<>()).add(coder);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return codersByCell;
    }

    @Override
    public void assignCoderToCell(String coderId, UUID cellId, UUID clanId) {
        String sql = """
                UPDATE "Cohorte"."user"
                SET cell_id = ?
                WHERE id = ? AND clan_id = ? AND UPPER(role) = ?
                """;

        try (PreparedStatement stmnt = getConnection().prepareStatement(sql)) {
            stmnt.setObject(1, cellId);
            stmnt.setObject(2, UUID.fromString(coderId));
            stmnt.setObject(3, clanId);
            stmnt.setString(4, Role.CODER.name());

            int updatedRows = stmnt.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Coder " + coderId + " was not assigned to the cell.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List getAll() {
        return List.of();
    }


    @Override
    public void update(Cell cell) {

    }

    @Override
    public Optional<Cell> findById(String id) {
        return Optional.empty();
    }
    
    public List<Cell> findByClanId(UUID clanId) {
        List<Cell> cells = new ArrayList<>();
        String sql = """
                SELECT id, name, clan_id
                FROM "Cohorte".cells
                WHERE clan_id = ?
                ORDER BY name
                """;

        try (PreparedStatement stmnt = getConnection().prepareStatement(sql)) {
            stmnt.setObject(1, clanId);

            try (ResultSet rs = stmnt.executeQuery()) {
                while (rs.next()) {
                    Cell cell = new Cell(
                            rs.getString("name"),
                            (UUID) rs.getObject("clan_id")
                    );
                    // Read UUID id from DB and store as String
                    Object rawId = rs.getObject("id");
                    if (rawId != null) {
                        cell.setId(rawId.toString());
                    }
                    cells.add(cell);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cells;
    }
    
    public void deleteCell(UUID cellId) {
        deleteCellById(cellId.toString());
    }

    @Override
    public void deleteCellById(String cellId) {
        // Parse to UUID to validate — throws IllegalArgumentException if invalid
        UUID uuid = UUID.fromString(cellId);

        String clearFk = """
                UPDATE "Cohorte"."user"
                SET cell_id = NULL
                WHERE cell_id = ?::uuid
                """;
        String sql = """
                DELETE FROM "Cohorte".cells
                WHERE id = ?::uuid
                """;

        try (PreparedStatement clearStmt = getConnection().prepareStatement(clearFk);
             PreparedStatement stmnt  = getConnection().prepareStatement(sql)) {

            clearStmt.setString(1, uuid.toString());
            clearStmt.executeUpdate();

            stmnt.setString(1, uuid.toString());
            int deletedRows = stmnt.executeUpdate();
            if (deletedRows == 0) {
                throw new RuntimeException("La célula no fue encontrada en la base de datos (id=" + cellId + ")");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la célula: " + e.getMessage(), e);
        }
    }
}
