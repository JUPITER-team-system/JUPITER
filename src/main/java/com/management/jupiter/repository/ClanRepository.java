package com.management.jupiter.repository;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * US-02 – Almacenamiento en memoria para Clan.
 * Corrige el paquete incorrecto (era "repositories") y los imports
 * para que coincidan con la estructura real del proyecto.
 */
public class ClanRepository {

    public final Map<String, Clan> clans = new HashMap<>();

    @Override
    public String toString() {
        return "ClanRepository{" +
                "clans=" + clans +
                '}';
    }

    
    public ClanRepository() {
        for (com.management.jupiter.models.enums.Clan clanEnum : com.management.jupiter.models.enums.Clan.values()) {
            Clan clan = new Clan(java.util.UUID.randomUUID().toString(), clanEnum.name());
            clans.put(clan.getId(), clan);
        }
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    public Clan save(Clan clan) {
        Clan newClan = new Clan(java.util.UUID.randomUUID().toString(), clan.getName());
        clans.put(newClan.getId(), newClan);
        return newClan;
    }

    // ── READ ALL ─────────────────────────────────────────────────────────────

    public List<Clan> findAll() {
        return new ArrayList<>(clans.values());
    }

    // ── READ BY ID ───────────────────────────────────────────────────────────

    public Clan findById(String id) {
        return clans.get(id);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void delete(String id) {
        clans.remove(id);
    }

    // ── VALIDACIONES ─────────────────────────────────────────────────────────

    public boolean existsByName(String name) {
        return clans.values().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    public boolean existsByNameExcludingId(String name, String excludeId) {
        return clans.values().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name) && !c.getId().equals(excludeId));
    }
}
