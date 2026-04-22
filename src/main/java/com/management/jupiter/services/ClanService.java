package com.management.jupiter.services;

import com.management.jupiter.models.Clan;
import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de Clanes.
 * La relación usuario↔clan se gestiona via user.clan_id (FK directa),
 * NO con una tabla intermedia clan_members.
 */
public class ClanService {

    private final ClanRepositoryImpl impl;

    /** Constructor para controladores FX (reciben la fachada ClanRepository). */
    public ClanService(ClanRepository facade) {
        this.impl = new ClanRepositoryImpl();
    }

    /** Constructor de compatibilidad con código legado. */
    public ClanService(ClanRepositoryImpl impl) {
        this.impl = impl;
    }

    public List<Clan> readAll() {
        try {
            return impl.getAll();
        } catch (Exception e) {
            System.err.println("[ClanService] Error reading clans: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Crea un nuevo clan (sin miembros inicialmente).
     * Lanza RuntimeException con mensaje claro si falla.
     */
    public Clan add(Clan clan) {
        try {
            var id = impl.save(clan);
            if (id != null) {
                clan = new Clan(id.toString(), clan.getName(), clan.getDescription());
            }
            return clan;
        } catch (Exception e) {
            throw new RuntimeException("Error creating clan: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un clan y desvincula sus usuarios.
     */
    public void delete(Clan clan) {
        try {
            impl.delete(clan.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting clan: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza nombre y descripción del clan.
     */
    public void edit(Clan clan) {
        try {
            impl.update(clan);
        } catch (Exception e) {
            throw new RuntimeException("Error updating clan: " + e.getMessage(), e);
        }
    }

    // ── Mensajes y Cells delegados al impl ──────────────────────────────────

    public List<String[]> getMessagesByClan(String clanId) {
        return impl.getMessagesByClan(clanId);
    }

    public String saveMessage(String clanId, String title, String message) {
        return impl.saveMessage(clanId, title, message);
    }

    public void deleteMessage(String messageId) {
        impl.deleteMessage(messageId);
    }

    public List<String[]> getCellsByClan(String clanId) {
        return impl.getCellsByClan(clanId);
    }

    public String saveCell(String clanId, String name) {
        return impl.saveCell(clanId, name);
    }

    public void deleteCell(String cellId) {
        impl.deleteCell(cellId);
    }
}
