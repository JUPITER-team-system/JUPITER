package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;

public class Coder extends User {

    public Coder(String username, String email, String password, Role role) {
        super(username, email, password, role);
    }

    public Coder(int id, String username, String email, String password, Role role) {
        super(id, username, email, password, role);
    }

    @Override
    public String toString() {
        return "id->[" + getId() + "] " + getUsername() + " (" + getRole() + ")";
    }
}
