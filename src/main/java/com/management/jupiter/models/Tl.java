package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;

import java.util.ArrayList;
import java.util.List;

/**
 * Un TL puede pertenecer a múltiples clanes simultáneamente (US-04).
 * Se elimina el atributo Clan fijo y se agrega TlType para distinguir
 * entre TL de programación y TL de inglés (necesario para validar límites).
 */
public class Tl extends User {

    private final TlType tlType;
    private List<Clan> clans;

    public Tl(String username, String email, String password, Role role, TlType tlType) {
        super(username, email, password, role, null);
        this.tlType = tlType;
        this.clans = new ArrayList<>();
    }

    public Tl(String id, String username, String email, String password, Role role, TlType tlType) {
        super(id, username, email, password, role, null);
        this.tlType = tlType;
        this.clans = new ArrayList<>();
    }

    public TlType getTlType() {
        return tlType;
    }

    public List<Clan> getClans() {
        return clans;
    }

    public void addClan(Clan clan) {
        if (clan != null && !clans.contains(clan)) {
            clans.add(clan);
        }
    }

    public void removeClan(Clan clan) {
        clans.remove(clan);
    }

    public boolean isAssignedToClan(Clan clan) {
        return clans.contains(clan);
    }

    @Override
    public String toString() {
        return "Tl{" + "TLName " + getUsername() +
                "tlType=" + tlType +
                ", clans=" + clans +
                '}';
    }
}
