package com.management.jupiter.services;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.CoderRepository;
import com.management.jupiter.repository.TeamLeaderRepository;

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

    private final ClanRepository        clanRepository;
    private final TeamLeaderRepository  tlRepository;
    private final CoderRepository       coderRepository;

    public AssignmentService(ClanRepository clanRepository,
                             TeamLeaderRepository tlRepository,
                             CoderRepository coderRepository) {

        this.clanRepository  = clanRepository;
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
        Clan clan = clanRepository.findById(clanId);

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
        Clan clan = clanRepository.findById(clanId);

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
        Clan  clan  = clanRepository.findById(clanId);

        clan.addCoder(coder);
    }

    /**
     * Desasigna un Coder de un clan.
     */
    public void desasignarCoderDeClan(String coderId, String clanId) {
        Coder coder = coderRepository.findById(coderId);
        Clan  clan  = clanRepository.findById(clanId);

        clan.removeCoder(coder);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CONSULTAS DE MIEMBROS POR CLAN
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Retorna la lista de TLs asignados a un clan.
     */
    public List<Tl> obtenerTlsDeClan(String clanId) {
        Clan clan = clanRepository.findById(clanId);
        return tlRepository.findAll().stream()
                .filter(tl -> tl.isAssignedToClan(clan))
                .collect(Collectors.toList());
    }

    /**
     * Retorna la lista de Coders asignados a un clan.
     */
    public List<Coder> obtenerCodersDeClan(String clanId) {
        return clanRepository.findById(clanId).getCoders();
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
        return clanRepository.findById(clanId).getTls().stream()
                .filter(tl -> tl.getTlType() == tipo)
                .collect(Collectors.toList());
    }

}
