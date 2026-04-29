package com.management.jupiter.services;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.repository.CoderRepository;

import java.util.UUID;

public class TeamLeaderService {
    private final CoderRepository coderRepository;

    public TeamLeaderService(CoderRepository coderRepository) {
        this.coderRepository = coderRepository;
    }

    public Coder addExistingCoderToClan(String coderEmailOrId, Clan clan) {
        if (coderEmailOrId == null || coderEmailOrId.isBlank()) {
            throw new IllegalArgumentException("Coder email or id is required.");
        }
        if (clan == null || clan.getId() == null || clan.getId().isBlank()) {
            throw new IllegalArgumentException("A valid clan is required.");
        }

        Coder coder = coderRepository.findByEmailOrId(coderEmailOrId.trim())
                .orElseThrow(() -> new IllegalArgumentException("Coder not found."));

        UUID clanId = UUID.fromString(clan.getId());
        coderRepository.findCoderClanId(coder.getId())
                .filter(clanId::equals)
                .ifPresent(currentClan -> {
                    throw new IllegalStateException("The coder already belongs to this clan.");
                });

        coderRepository.assignCoderToClan(coder.getId(), clanId);
        return coder;
    }

    public Coder removeCoderFromClan(String coderEmailOrId, Clan clan) {
        if (coderEmailOrId == null || coderEmailOrId.isBlank()) {
            throw new IllegalArgumentException("Coder email or id is required.");
        }
        if (clan == null || clan.getId() == null || clan.getId().isBlank()) {
            throw new IllegalArgumentException("A valid clan is required.");
        }

        Coder coder = coderRepository.findByEmailOrId(coderEmailOrId.trim())
                .orElseThrow(() -> new IllegalArgumentException("Coder not found."));

        UUID clanId = UUID.fromString(clan.getId());
        UUID currentClanId = coderRepository.findCoderClanId(coder.getId())
                .orElseThrow(() -> new IllegalStateException("The coder does not belong to any clan."));

        if (!clanId.equals(currentClanId)) {
            throw new IllegalStateException("The coder does not belong to this clan.");
        }

        coderRepository.unassignCoderFromClan(coder.getId(), clanId);
        return coder;
    }
}
