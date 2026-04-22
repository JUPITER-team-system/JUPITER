package com.management.jupiter.repository;

import com.management.jupiter.models.Clan;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Fachada sobre ClanRepositoryImpl para uso en controladores FX.
 * Delega toda la lógica al impl, evitando duplicación.
 */
public class ClanRepository {

    private final ClanRepositoryImpl impl = new ClanRepositoryImpl();

    // ── Clan CRUD ──────────────────────────────────────────────────────────
    public List<Clan> getAll()                            { return impl.getAll(); }
    public Optional<Clan> findById(String id)             { return impl.findById(id); }
    public Optional<Clan> findByIdOrName(String value)    { return impl.findByIdOrName(value); }
    public UUID save(Clan clan)                           { return impl.save(clan); }
    public void update(Clan clan)                         { impl.update(clan); }
    public void delete(String id)                         { impl.delete(id); }

    // ── Asignación usuario↔clan (via user.clan_id) ─────────────────────────
    public void addUser(UUID clanId, String userId) throws SQLException  { impl.addUser(clanId, userId); }
    public void removeUser(String clanId)           throws SQLException  { impl.removeUser(clanId); }

    // ── Messages ───────────────────────────────────────────────────────────
    public List<String[]> getMessagesByClan(String clanId)                { return impl.getMessagesByClan(clanId); }
    public String saveMessage(String clanId, String title, String message) { return impl.saveMessage(clanId, title, message); }
    public void deleteMessage(String messageId)                           { impl.deleteMessage(messageId); }

    // ── Cells ──────────────────────────────────────────────────────────────
    public List<String[]> getCellsByClan(String clanId)   { return impl.getCellsByClan(clanId); }
    public String saveCell(String clanId, String name)    { return impl.saveCell(clanId, name); }
    public void deleteCell(String cellId)                 { impl.deleteCell(cellId); }
}
