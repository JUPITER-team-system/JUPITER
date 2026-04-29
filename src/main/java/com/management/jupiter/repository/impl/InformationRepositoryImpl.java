package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Information;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.InformationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InformationRepositoryImpl implements InformationRepository {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public void save(Information information) {
        String sql = """
                INSERT INTO "Cohorte".information(title, message, clan_id)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, information.getTitle());
            stmt.setString(2, information.getMessage());
            stmt.setObject(3, UUID.fromString(information.getClanId()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Information> findByClanId(UUID clanId) {
        String sql = """
                SELECT id, title, message, clan_id, created_at
                FROM "Cohorte".information
                WHERE clan_id = ?
                ORDER BY created_at DESC
                """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, clanId);

            try (ResultSet rs = stmt.executeQuery()) {
                return mapInformation(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Information> findByUserClan(String userId) {
        String sql = """
                SELECT i.id, i.title, i.message, i.clan_id, i.created_at
                FROM "Cohorte".information i
                INNER JOIN "Cohorte"."user" u ON u.clan_id = i.clan_id
                WHERE u.id = ?
                ORDER BY i.created_at DESC
                """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));

            try (ResultSet rs = stmt.executeQuery()) {
                return mapInformation(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Information> mapInformation(ResultSet rs) throws SQLException {
        List<Information> informationList = new ArrayList<>();

        while (rs.next()) {
            informationList.add(new Information(
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getString("clan_id"),
                    rs.getObject("created_at", OffsetDateTime.class)
            ));
        }

        return informationList;
    }
}
