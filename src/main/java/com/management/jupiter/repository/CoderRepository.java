package com.management.jupiter.repository;

import com.management.jupiter.models.Coder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio en memoria para Coders.
 * Necesario para que AssignmentService pueda buscar Coders por ID (US-04).
 */
public class CoderRepository {

    private final Map<Integer, Coder> coders = new HashMap<>();

    // ── CREATE / UPDATE ──────────────────────────────────────────────────────

    public void save(Coder coder) {
        coders.put(coder.getId(), coder);
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    public List<Coder> findAll() {
        return new ArrayList<>(coders.values());
    }

    public Coder findById(int id) {
        return coders.get(id);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void delete(int id) {
        coders.remove(id);
    }
}
