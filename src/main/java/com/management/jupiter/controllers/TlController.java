package com.management.jupiter.controllers;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.services.TeamLeaderService;

public class TlController {
    private final TeamLeaderService teamLeaderService;

    public TlController(TeamLeaderService teamLeaderService) {
        this.teamLeaderService = teamLeaderService;
    }

    public Coder addExistingCoderToClan(String coderEmailOrId, Clan clan) {
        if (coderEmailOrId == null || coderEmailOrId.isBlank() || clan == null) {
            throw new IllegalArgumentException("All fields are required.");
        }

        return teamLeaderService.addExistingCoderToClan(coderEmailOrId, clan);
    }

    public Coder removeCoderFromClan(String coderEmailOrId, Clan clan) {
        if (coderEmailOrId == null || coderEmailOrId.isBlank() || clan == null) {
            throw new IllegalArgumentException("All fields are required.");
        }

        return teamLeaderService.removeCoderFromClan(coderEmailOrId, clan);
    }
}
