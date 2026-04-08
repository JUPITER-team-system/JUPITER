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

    private final Map<Integer, Clan> clans = new HashMap<>();
    private int currentId = 1;

    public ClanRepository() {
        for (com.management.jupiter.models.enums.Clan clanEnum : com.management.jupiter.models.enums.Clan.values()) {
            Clan clan = new Clan(currentId++, clanEnum.name());
            clans.put(clan.getId(), clan);
        }
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    public Clan save(Clan clan) {
        Clan newClan = new Clan(currentId++, clan.getName());
        clans.put(newClan.getId(), newClan);
        return newClan;
    }

    // ── READ ALL ─────────────────────────────────────────────────────────────

    public List<Clan> findAll() {
        return new ArrayList<>(clans.values());
    }

    // ── READ BY ID ───────────────────────────────────────────────────────────

    public Clan findById(int id) {
        return clans.get(id);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void delete(int id) {
        clans.remove(id);
    }

    // ── VALIDACIONES ─────────────────────────────────────────────────────────

    public boolean existsByName(String name) {
        return clans.values().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    public boolean existsByNameExcludingId(String name, int excludeId) {
        return clans.values().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name) && c.getId() != excludeId);
    }
}
