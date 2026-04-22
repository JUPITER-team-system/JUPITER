package com.management.jupiter.repository.impl;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.*;
import com.management.jupiter.models.enums.*;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.ClanRepository;

import java.sql.*;
import java.util.*;

public class ClanRepositoryImpl implements ClanRepository {

    @Override
    public List<Clan> getAll() {

        Map<String, Clan> clanMap = new LinkedHashMap<>();

        String sql = """
                SELECT
                    c.id AS clan_id,
                    c.name AS clan_name,
                    c.description AS clan_description,
                    u.id AS user_id,
                    u.full_name AS user_name,
                    u.email AS user_email,
                    u.specialty AS user_specialty,
                    u.role
                FROM "Cohorte".clan c
                LEFT JOIN "Cohorte".clan_members cm ON c.id = cm.clan_id
                LEFT JOIN "Cohorte"."user" u ON cm.user_id = u.id
                """;

        try {

            Connection conn = DatabaseConnection.getConnection();

            try (PreparedStatement psmt = conn.prepareStatement(sql);
                 ResultSet rs = psmt.executeQuery()
            ){

                while (rs.next()){

                    String clanId = rs.getString("clan_id");

                    Clan clan = clanMap.computeIfAbsent(clanId, id -> {

                        try {

                            return new Clan(
                                    id,
                                    rs.getString("clan_name"),
                                    rs.getString("clan_description")
                            );

                        }catch (SQLException e) {

                            throw new RuntimeException(e);

                        }

                    });

                    String userId = rs.getString("user_id");

                    if (userId != null){

                        var name = rs.getString("user_name");
                        var email = rs.getString("user_email");
                        var role = rs.getString("role").toUpperCase();

                        if ("Tl".equalsIgnoreCase(role)) {

                            var specialty = rs.getString("user_specialty").toUpperCase();

                            clan.getTls().add(new Tl(
                                    userId,
                                    name,
                                    email,
                                    null,
                                    Role.valueOf(role),
                                    TlType.valueOf(specialty)
                            ));

                        } else {

                            clan.getCoders().add(new Coder(
                                    userId,
                                    name,
                                    email,
                                    null,
                                    Role.valueOf(role)
                            ));

                        }
                    }

                }

            }

        }catch (SQLException err) {

            throw new RuntimeException("Error to get clan: " + err.getMessage());

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
                    c.id AS clan_id,
                    c.name AS clan_name,
                    c.description AS clan_description,
                    u.id AS user_id,
                    u.full_name AS user_name,
                    u.email AS user_email,
                    u.specialty AS user_specialty,
                    u.role
                FROM "Cohorte".clan c
                LEFT JOIN "Cohorte".clan_members cm ON c.id = cm.clan_id
                LEFT JOIN "Cohorte"."user" u ON cm.user_id = u.id
                WHERE c.name = ? or c.id = ?;
                """;

        try {

            Connection conn = DatabaseConnection.getConnection();

            try (PreparedStatement psmt = conn.prepareStatement(sql)){

                psmt.setString(1, value);

                try {
                    psmt.setObject(2, UUID.fromString(value));
                }catch (IllegalArgumentException err){
                    psmt.setObject(2, null);
                }

                try(ResultSet rs = psmt.executeQuery()){

                    while (rs.next()){

                        String idClanActual = rs.getString("clan_id");

                        Clan clan = clanMap.computeIfAbsent(idClanActual, id -> {
                            try {

                                return new Clan(
                                        id,
                                        rs.getString("clan_name"),
                                        rs.getString("clan_description"))
                                        ;

                            }catch (SQLException e) {

                                throw new RuntimeException(e);

                            }

                        });

                        String userId = rs.getString("user_id");

                        if (userId != null){

                            var name = rs.getString("user_name");
                            var email = rs.getString("user_email");
                            var role = rs.getString("role").toUpperCase();

                            if ("Tl".equalsIgnoreCase(role)) {

                                var specialty = rs.getString("user_specialty").toUpperCase();

                                clan.getTls().add(new Tl(
                                        userId,
                                        name,
                                        email,
                                        null,
                                        Role.valueOf(role),
                                        TlType.valueOf(specialty)
                                ));

                            } else {

                                clan.getCoders().add(new Coder(
                                        userId,
                                        name,
                                        email,
                                        null,
                                        Role.valueOf(role)
                                ));

                            }
                        }
                    }
                }

            }

        }catch (SQLException err){

            throw new RuntimeException("Error to get clan: " + err.getMessage());

        }

        return clanMap.values().stream().findFirst();

    }

    @Override
    public UUID save (Clan clanData) {

        String sql = "INSERT INTO \"Cohorte\".clan (name, description) Values (?, ?)";

        try {

            Connection conn = DatabaseConnection.getConnection();

            try (PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){


                psmt.setString(1, clanData.getName());
                psmt.setString(2, clanData.getDescription());

                int arrows = psmt.executeUpdate();

                if (arrows > 0){

                    var rs = psmt.getGeneratedKeys();
                    if (rs.next()) {
                        return (UUID) rs.getObject(1);
                    }

                }else {

                    throw new SQLException("Error to add clan information");

                }

            }

        }catch (SQLException err) {

            throw new RuntimeException("Error to save clan: " + err.getMessage());

        }

        return null;

    }

    @Override
    public void delete (String value) {

        String sql = "DELETE FROM \"Cohorte\".clan WHERE name = ? or id = ?";

        try {

            Connection conn = DatabaseConnection.getConnection();

            try (PreparedStatement pstm = conn.prepareStatement(sql)){

                pstm.setString(1, value);

                try {
                    pstm.setObject(2, UUID.fromString(value));
                }catch (IllegalArgumentException err){
                    pstm.setObject(2, null);
                }

                int rows = pstm.executeUpdate();

                if (rows == 0){

                    System.out.println("Error at delete clan");

                }else {

                    System.out.println("Clan deleted correctly");

                }

            }

        }catch(SQLException err){

            throw new RuntimeException("Error to delete clan: " + err.getMessage());

        }

    }

    @Override
    public void update(Clan clan) {

        String sql = "UPDATE \"Cohorte\".clan SET name = ?, description = ? WHERE id = ?";

        try {

            Connection conn = DatabaseConnection.getConnection();

            try (PreparedStatement psmt = conn.prepareStatement(sql)){


                psmt.setString(1, clan.getName());
                psmt.setString(2, clan.getDescription());
                psmt.setObject(3, UUID.fromString(clan.getId()));

                int rows = psmt.executeUpdate();

                if (rows == 0){

                    throw new SQLException("Error to edit Clan");

                }

            }

        }catch (SQLException err) {

            throw new RuntimeException("Error to update clan: " + err.getMessage());

        }

    }

    @Override
    public void addUser (UUID clanId , String userId) throws SQLException {

        String sql = "INSERT INTO \"Cohorte\".clan_members (clan_id, user_id) values (?, ?)";

        Connection conn = DatabaseConnection.getConnection();

        try(PreparedStatement psmt = conn.prepareStatement(sql)){

            psmt.setObject(1, clanId);
            psmt.setObject(2, UUID.fromString(userId));

            int rows = psmt.executeUpdate();

            if (rows == 0) {

                throw new SQLException("Error to add user");

            }else {

                System.out.println("User added correctly");

            }

        }

    }

    @Override
    public void removeUser (String clanId) throws SQLException{

        String sql = "DELETE FROM \"Cohorte\".clan_members WHERE clan_id = ?";

        Connection conn = DatabaseConnection.getConnection();

        try(PreparedStatement psmt = conn.prepareStatement(sql)){

            psmt.setObject(1, UUID.fromString(clanId));

            int rows = psmt.executeUpdate();

            if (rows == 0){

                System.out.println("has cleaned " + rows + " old members.");

            }

        }

    }

}