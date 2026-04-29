package com.management.jupiter.services;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Information;
import com.management.jupiter.models.User;
import com.management.jupiter.repository.interfaces.InformationRepository;

import java.util.List;
import java.util.UUID;

public class InformationService {
    private static final int TITLE_MAX_LENGTH = 40;

    private final InformationRepository informationRepository;

    public InformationService(InformationRepository informationRepository) {
        this.informationRepository = informationRepository;
    }

    public void createInformation(String title, String message, Clan clan) {
        if (clan == null || clan.getId() == null || clan.getId().isBlank()) {
            throw new IllegalStateException("A valid clan is required to create information.");
        }

        validateTitle(title);
        validateMessage(message);

        informationRepository.save(new Information(title.trim(), message.trim(), clan.getId()));
    }

    public List<Information> findByClan(Clan clan) {
        if (clan == null || clan.getId() == null || clan.getId().isBlank()) {
            throw new IllegalStateException("A valid clan is required to view information.");
        }

        return informationRepository.findByClanId(UUID.fromString(clan.getId()));
    }

    public List<Information> findByUserClan(User user) {
        if (user == null || user.getId() == null || user.getId().isBlank()) {
            throw new IllegalStateException("A valid user is required to view information.");
        }

        return informationRepository.findByUserClan(user.getId());
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("The title is required.");
        }
        if (title.trim().length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("The title cannot have more than 40 characters.");
        }
    }

    private void validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("The message is required.");
        }
    }
    
    public void deleteInformation(String informationId) {
        if (informationId == null || informationId.isBlank()) {
            throw new IllegalArgumentException("The information ID is required.");
        }
        
        informationRepository.delete(informationId);
    }
}
