package com.management.jupiter.models;

import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;

public class Coder extends User {
    private final Clan clan;

    public Coder(String username, String email, String password, Role role, Clan clan) {
        super(username, email, password, role);
        this.clan = clan;
    }

    public Clan getClan() { return clan; }

    @Override
    public String toString() {
        return "id->[" + getId() + "] " + getUsername() + " (" + getRole() + ") " + clan;
    }
}
