package com.management.jupiter.repository;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.*;
import com.management.jupiter.models.enums.*;
import com.management.jupiter.persistance.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ClanRepository {

    public List<Clan> findAll (){

        Map<String, Clan> clanMap = new LinkedHashMap<>();

        String sql = """
                SELECT
                    c.id AS clan_id
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

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery();
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


        }catch (SQLException err) {

            System.err.println("Error to get clan: " + err.getMessage());

        }


        return new ArrayList<>(clanMap.values());

    }

    public Clan findById(String clanId) {

        Map<String, Clan> clanMap = new LinkedHashMap<>();

        String sql = """
                SELECT
                    c.id AS clan_id
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
                WHERE c.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ){

            psmt.setObject(1, UUID.fromString(clanId));

            try(ResultSet rs = psmt.executeQuery()){

                while (rs.next()){


                    Clan clan = clanMap.computeIfAbsent(clanId, id -> {
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

        }catch (SQLException err){

            System.err.println("Error to get clan: " + err.getMessage());

        }

        return clanMap.get(clanId);

    }

    public String findByName (String name) {

        String sql = "SELECT clan.id AS clan_id FROM clan where name = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement psmt = conn.prepareStatement(sql)){

            psmt.setString(1, name);

            try (ResultSet rs = psmt.executeQuery();){

                if (rs.next()){

                    return rs.getString("clan_id");

                }

            }

        }catch (SQLException err){

            System.err.println("Error to get clan by name: " + err.getMessage());

        }

        return null;

    }

    public UUID save (Clan clanData, Connection conn) throws SQLException {

        String sql = "INSERT INTO clan (name, description) Values (?, ?)";

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

        return null;

    }

    public void delete (String id) {

        String sql = "DELETE FROM clan WHERE ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)){


            pstm.setObject(1, UUID.fromString(id));

            int rows = pstm.executeUpdate();

            if (rows == 0){

                System.out.println("Error at delete clan");

            }else {

                System.out.println("Clan deleted correctly");

            }

        }catch(SQLException err){

            System.err.println("Error to delete clan: " + err.getMessage());

        }

    }

    public void edit (Clan clan, Connection conn) throws SQLException {

        String sql = "UPDATE clan SET name = ?, description = ? WHERE id = ?";

        try (PreparedStatement psmt = conn.prepareStatement(sql)){


            psmt.setString(1, clan.getName());
            psmt.setString(2, clan.getDescription());
            psmt.setObject(3, UUID.fromString(clan.getId()));

            int rows = psmt.executeUpdate();

            if (rows == 0){

                throw new SQLException("Error to edit Clan");

            }

        }

    }


    public void addUser (UUID clanId ,String userId, Connection conn) throws SQLException {

        String sql = "INSERT INTO clan_members (clan_id, user_id) values (?, ?)";

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

    public void removeUser (String clanId, Connection conn) throws SQLException{

        String sql = "DELETE FROM clan_members WHERE clan_id = ?";

        try(PreparedStatement psmt = conn.prepareStatement(sql)){

            psmt.setObject(1, UUID.fromString(clanId));

            int rows = psmt.executeUpdate();

            if (rows == 0){

                System.out.println("has cleaned " + rows + " old members.");

            }

        }

    }

}