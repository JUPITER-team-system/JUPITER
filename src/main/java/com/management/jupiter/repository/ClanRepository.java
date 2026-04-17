package com.management.jupiter.repository;

import com.management.jupiter.models.Clan;
import com.management.jupiter.persistance.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ClanRepository {

    public List<Clan> findAll() {

        List<Clan> clanList = new ArrayList<>();
        String sql = "SELECT * FROM clan";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql);){



            while (rs.next()){

                String clanId = rs.getString("id");
                String clanName = rs.getString("name");
                String clanDesc = rs.getString("description");
                String userId = rs.getString("user_id");

                Clan clan = new Clan(clanId, clanName, clanDesc, userId);

                clanList.add(clan);

            }

        }catch (SQLException err){

            System.err.println("Error to get clans: " + err.getMessage());

        }

        return clanList;

    }

    public UUID save (Clan clanData, Connection conn) throws SQLException {

        String sql = "INSERT INTO clan (name, description) Values (?, ?)";

        try (PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){


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
             PreparedStatement pstm = conn.prepareStatement(sql);){


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

    public boolean edit (Clan clan) {

        String sql = "UPDATE clan SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)){


            psmt.setString(1, clan.getName());
            psmt.setString(2, clan.getDescription());
            psmt.setObject(3, UUID.fromString(clan.getId()));

            int rows = psmt.executeUpdate();

            return rows > 0;

        }catch (SQLException err){

            System.err.println("Error to update clan: " + err.getMessage());

        }

        return false;

    }


    public void addUser (UUID clanId ,String userId, Connection conn) throws SQLException {

        String sql = "INSERT INTO clan_members (clan_id, user_id) values (?, ?)";

        try(PreparedStatement psmt = conn.prepareStatement(sql)){

            psmt.setObject(1, clanId);
            psmt.setObject(2, UUID.fromString(userId));

            int rows = psmt.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Error to add user");
            }

            System.out.println("User added correctly");

        }

    }

}