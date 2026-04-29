package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;

public class Coder extends User {

    public Coder(String username, String email, String password, Role role) {
        super(username, email, password, role, null);
    }

    public Coder(String id, String username, String email, String password, Role role) {
        super(id, username, email, password, role, null);
    }

    /**
     * Constructor con clan - necesario para que el Coder conserve su clan
     * y el sistema pueda cargar mensajes del clan correctamente.
     */
    public Coder(String id, String username, String email, String password, Role role, Clan clan) {
        super(id, username, email, password, role, clan);
    }

    @Override
    public String toString() {
        return "id->[" + getId() + "] " + getUsername() + " (" + getRole() + ")" +
               (getClan_id() != null ? " - Clan: " + getClan_id().getName() : "");
    }
}
