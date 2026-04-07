package com.management.jupiter.services;

import com.management.jupiter.models.Clan;
import com.management.jupiter.repository.ClanRepository;

import java.util.List;

/**
 * US-02 – Lógica CRUD completa para Clan.
 * Corrige el paquete incorrecto (era "services") y los imports
 * para que coincidan con la estructura real del proyecto.
 */
public class ClanService {

    private final ClanRepository repository;

    public ClanService() {
        this.repository = new ClanRepository();
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    public Clan crearClan(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del clan no puede estar vacío.");
        }
        if (repository.existsByName(name)) {
            throw new IllegalStateException("Ya existe un clan con el nombre: " + name);
        }
        Clan clan = new Clan(0, name.trim());
        return repository.save(clan);
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    public List<Clan> listarClanes() {
        return repository.findAll();
    }

    public Clan buscarPorId(int id) {
        Clan clan = repository.findById(id);
        if (clan == null) {
            throw new IllegalArgumentException("No existe un clan con id: " + id);
        }
        return clan;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    public void actualizarClan(int id, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("El nuevo nombre no puede estar vacío.");
        }

        Clan clan = repository.findById(id);
        if (clan == null) {
            throw new IllegalArgumentException("Clan no encontrado con id: " + id);
        }

        // Permite actualizar con el mismo nombre (sin cambio real), pero bloquea
        // si ese nombre ya lo usa OTRO clan.
        if (repository.existsByNameExcludingId(nuevoNombre, id)) {
            throw new IllegalStateException("Ese nombre ya está en uso por otro clan.");
        }

        clan.setName(nuevoNombre.trim());
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Criterio de aceptación: no se puede eliminar un clan con coders activos.
     */
    public void eliminarClan(int id) {
        Clan clan = repository.findById(id);
        if (clan == null) {
            throw new IllegalArgumentException("No existe un clan con id: " + id);
        }
        if (clan.hasCoders()) {
            throw new IllegalStateException(
                    "No se puede eliminar el clan '" + clan.getName() +
                    "' porque tiene " + clan.getCoders().size() + " coder(s) asignado(s).");
        }
        repository.delete(id);
    }
}
