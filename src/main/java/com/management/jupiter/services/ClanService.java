package com.management.jupiter.services;

import com.management.jupiter.models.*;
import com.management.jupiter.persistance.DatabaseConnection;
import com.management.jupiter.repository.interfaces.ClanRepository;

import java.sql.SQLException;
import java.util.*;

public class ClanService {

    private final ClanRepository clanRepo;

    public ClanService (ClanRepository clanRepo) {

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

        try {

            DatabaseConnection.startTransaction();

            UUID data = clanRepo.save(clan);

            for (User coder : clan.getCoders()){

                clanRepo.addUser(data, coder.getId());

            }

            for (User tl : clan.getTls()){

                clanRepo.addUser(data, tl.getId());

            }

            DatabaseConnection.commit();
            System.out.println("Users added correctly");

        }catch (SQLException err){

            DatabaseConnection.rollback();
            System.err.println("Revert transfer:" + err.getMessage());

        }


    }

    public void delete (String value) {

        clanRepo.delete(value);

    }

    public void edit (Clan clan) {

            try{

                DatabaseConnection.startTransaction();

                var clanId = clan.getId();

                clanRepo.update(clan);

                clanRepo.removeUser(clanId);

                for (User coder : clan.getCoders()){

                    clanRepo.addUser(UUID.fromString(clanId), coder.getId());

                }

                for (User tl : clan.getTls()){

                    clanRepo.addUser(UUID.fromString(clanId), tl.getId());

                }

                DatabaseConnection.commit();

            }catch (SQLException err){

                DatabaseConnection.rollback();
                System.err.println("Revert transfer:" + err.getMessage());

            }

    }

}