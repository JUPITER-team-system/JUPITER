package com.management.jupiter.services;

import com.management.jupiter.models.*;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ClanService {

    private final ClanRepositoryImpl clanRepo;

    public ClanService (ClanRepositoryImpl clanRepo) {

        this.clanRepo = clanRepo;

    }

    public List<Clan> readAll () {

        List<Clan> clanList = new ArrayList<>();

        try {

            clanList = clanRepo.getAll();

        } catch (Exception err) {

            System.err.println("Error to obtain clans: " + err.getMessage());

        }

        return clanList;

    }

    public void add (Clan clan) {

        try(Connection conn = DatabaseConnection.getConnection()){

            try {

                conn.setAutoCommit(false);

                UUID data = clanRepo.save(clan, conn);

                for (User coder : clan.getCoders()){

                    clanRepo.addUser(data, coder.getId(), conn);

                }

                for (User tl : clan.getTls()){

                    clanRepo.addUser(data, tl.getId(), conn);

                }

                conn.commit();
                System.out.println("Users added correctly");

            }catch (SQLException err){

                conn.rollback();
                System.err.println("Revert transfer:" + err.getMessage());

            }

        }catch (Exception err){

            System.err.println("Error to add Users: " + err.getMessage());

        }

    }

    public void delete (Clan clan) {

        clanRepo.delete(clan.getId());

    }

    public void edit (Clan clan) {

        try(Connection conn = DatabaseConnection.getConnection()){

            try{

                conn.setAutoCommit(false);

                var clanId = clan.getId();

                clanRepo.update(clan, conn);

                clanRepo.removeUser(clanId, conn);

                for (User coder : clan.getCoders()){

                    clanRepo.addUser(UUID.fromString(clanId), coder.getId(), conn);

                }

                for (User tl : clan.getTls()){

                    clanRepo.addUser(UUID.fromString(clanId), tl.getId(), conn);

                }

                conn.commit();

            }catch (SQLException err){

                conn.rollback();
                System.err.println("Revert transfer:" + err.getMessage());

            }

        }catch (Exception err){

            System.err.println("Error to add Users: " + err.getMessage());

        }

    }


}