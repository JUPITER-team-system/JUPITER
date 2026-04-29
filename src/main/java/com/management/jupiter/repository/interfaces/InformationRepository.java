package com.management.jupiter.repository.interfaces;

import com.management.jupiter.models.Information;

import java.util.List;
import java.util.UUID;

public interface InformationRepository {
    void save(Information information);
    List<Information> findByClanId(UUID clanId);
    List<Information> findByUserClan(String userId);
    void delete(String informationId);
}
