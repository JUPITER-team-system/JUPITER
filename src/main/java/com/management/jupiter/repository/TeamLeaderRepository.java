package com.management.jupiter.repository;

import com.management.jupiter.models.Tl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio en memoria para TLs.
 * Necesario para que AssignmentService pueda buscar TLs por ID (US-04).
 */
public class TeamLeaderRepository {

    private final Map<Integer, Tl> tls = new HashMap<>();

    // ── CREATE / UPDATE ──────────────────────────────────────────────────────

    public void save(Tl tl) {
        tls.put(tl.getId(), tl);
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    public List<Tl> findAll() {
        return new ArrayList<>(tls.values());
    }

    public Tl findById(int id) {
        return tls.get(id);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void delete(int id) {
        tls.remove(id);
    }
}
