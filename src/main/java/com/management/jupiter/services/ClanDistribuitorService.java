package com.management.jupiter.services;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.User;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;

import java.util.List;

public class ClanDistribuitorService {
    private final AdminRepositoryImpl adminRepository;
    private final ClanRepositoryImpl clanRepository;

    public ClanDistribuitorService(AdminRepositoryImpl adminRepository, ClanRepositoryImpl clanRepository){
        this.clanRepository = clanRepository;
        this.adminRepository = adminRepository;
    }

    public void  runDistribution(){
        final int MAX_CAPACITY = 40; // limite de clan
        List<User> unnasignUsers = adminRepository.findUsersWhitouClan();
        List<Clan> allClans = clanRepository.getAll();

        List<Clan> avaliableClans = allClans.stream()
                .filter(clan -> clan.getCoders().size() < MAX_CAPACITY).toList();
        if (avaliableClans.isEmpty()){
            System.out.println("[FATAL ERROR]: All clans have their maximum capacity");
            return;
        }

        if (unnasignUsers.isEmpty()){
            System.out.println("All users is in a clan");
            return;
        }
        //Motor de reparto
        int clanIndex = 0;

        for (User user : unnasignUsers) {
            Clan targetClan = avaliableClans.get(clanIndex);

            //Asignacion en db
            adminRepository.assignClan(user.getId(), targetClan.getId());
            //Le avisamos de manera local a JVM que se esta llenando
            targetClan.getCoders().add((com.management.jupiter.models.Coder) user);

            System.out.println("Assigned: " + user.getUsername() + "--->" + targetClan.getName());
            clanIndex = (clanIndex + 1) % avaliableClans.size();

            if (targetClan.getCoders().size() >= MAX_CAPACITY){
                System.out.println("All clans have reached their maximum capacity");

            }
        }
        System.out.println("Distribution success");

    }
}
