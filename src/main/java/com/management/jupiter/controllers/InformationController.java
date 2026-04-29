package com.management.jupiter.controllers;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Information;
import com.management.jupiter.models.User;
import com.management.jupiter.services.InformationService;

import java.util.List;

public class InformationController {
    private final InformationService informationService;

    public InformationController(InformationService informationService) {
        this.informationService = informationService;
    }

    public void createInformation(String title, String message, Clan clan) {
        informationService.createInformation(title, message, clan);
    }

    public List<Information> findByClan(Clan clan) {
        return informationService.findByClan(clan);
    }

    public List<Information> findByUserClan(User user) {
        return informationService.findByUserClan(user);
    }
    
    public void deleteInformation(String informationId) {
        informationService.deleteInformation(informationId);
    }
}
