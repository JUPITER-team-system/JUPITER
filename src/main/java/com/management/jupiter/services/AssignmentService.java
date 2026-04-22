package com.management.jupiter.services;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;
import com.management.jupiter.repository.CoderRepository;
import com.management.jupiter.repository.impl.TeamLeaderRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * US-04 – Asignación de TLs y Coders a Clanes.
 *
 * Reglas de negocio:
 *   - Máximo 1 TL de PROGRAMACION por clan.
 *   - Máximo 2 TLs de INGLES por clan.
 *   - Un TL puede pertenecer a múltiples clanes simultáneamente.
 */
public class AssignmentService {

    private final ClanRepositoryImpl clanRepositoryImpl;
    private final TeamLeaderRepositoryImpl tlRepository;
    private final CoderRepository       coderRepository;

    public AssignmentService(ClanRepositoryImpl clanRepositoryImpl,
                             TeamLeaderRepositoryImpl tlRepository,
                             CoderRepository coderRepository) {

        this.clanRepositoryImpl = clanRepositoryImpl;
        this.tlRepository    = tlRepository;
        this.coderRepository = coderRepository;

    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ASIGNACIÓN TL → CLAN
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Asigna un TL a un clan respetando los límites establecidos.
     *
     * @param tlId    ID del TL a asignar.
     * @param clanId  ID del clan destino.
     */
    public void clanTls(String tlId, String clanId) {

        Tl   tl   = tlRepository.findById(tlId);
        Clan clan = clanRepositoryImpl.findByIdOrName(clanId).orElseThrow();

        // Verificar que el TL no esté ya asignado a este clan
        if (clan.hasTl(tl)) {
            throw new IllegalStateException(
                    "El TL '" + tl.getUsername() + "' ya está asignado al clan '" + clan.getName() + "'.");
        }

        // Contar cuántos TLs del mismo tipo tiene el clan
        long countMismoTipo = clan.getTls().stream()
                .filter(t -> t.getTlType() == tl.getTlType())
                .count();

        // Realizar asignación (bidireccional)
        clan.addTl(tl);
        tl.addClan(clan);
        tlRepository.save(tl);
    }

    /**
     * Desasigna un TL de un clan.
     */
    public void desasignarTlDeClan(String tlId, String clanId) {
        Tl   tl   = tlRepository.findById(tlId);
        Clan clan = clanRepositoryImpl.findByIdOrName(clanId).orElseThrow();

        if (!clan.hasTl(tl)) {
            throw new IllegalStateException(
                    "El TL '" + tl.getUsername() + "' no está asignado al clan '" + clan.getName() + "'.");
        }

        clan.removeTl(tl);
        tl.removeClan(clan);
        tlRepository.save(tl);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ASIGNACIÓN CODER → CLAN
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Asigna un Coder a un clan.
     */
    public void asignarCoderAClan(String coderId, String clanId) {
        Coder coder = coderRepository.findById(coderId);
        Clan  clan  = clanRepositoryImpl.findByIdOrName(clanId).orElseThrow();

        clan.addCoder(coder);
    }

    /**
     * Desasigna un Coder de un clan.
     */
    public void desasignarCoderDeClan(String coderId, String clanId) {
        Coder coder = coderRepository.findById(coderId);
        Clan  clan  = clanRepositoryImpl.findByIdOrName(clanId).orElseThrow();

        clan.removeCoder(coder);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CONSULTAS DE MIEMBROS POR CLAN
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Retorna la lista de TLs asignados a un clan.
     */
    public List<Tl> obtenerTlsDeClan(String clanId) {
        Clan clan = clanRepositoryImpl.findByIdOrName(clanId).orElseThrow();
        return tlRepository.findAll().stream()
                .filter(tl -> tl.isAssignedToClan(clan))
                .collect(Collectors.toList());
    }

    /**
     * Retorna la lista de Coders asignados a un clan.
     */
    public List<Coder> obtenerCodersDeClan(String clanId) {
        return clanRepositoryImpl.findByIdOrName(clanId).orElseThrow().getCoders();
    }

    /**
     * Retorna todos los clanes a los que pertenece un TL.
     */
    public List<Clan> obtenerClanesDeTl(String tlId) {
        return tlRepository.findById(tlId).getClans();
    }

    /**
     * Retorna los TLs de un tipo específico dentro de un clan.
     */
    public List<Tl> obtenerTlsDeClanPorTipo(String clanId, TlType tipo) {
        return clanRepositoryImpl.findByIdOrName(clanId).orElseThrow().getTls().stream()
                .filter(tl -> tl.getTlType() == tipo)
                .collect(Collectors.toList());
    }

}
