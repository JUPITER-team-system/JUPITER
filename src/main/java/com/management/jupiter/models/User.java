package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;

import java.util.Objects;

public class User {
    private static int counter = 1;

    private final int    id;
    private final String username;
    private final String email;
    private final String password;
    private final Role   role;
    private final Clan clan_id;

    public User(String username, String email, String password, Role role, Clan clan_id) {
        this.id       = counter++;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
        this.clan_id = clan_id;
    }

    public User(int id, String username, String email, String password, Role role, Clan clanId) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
        this.clan_id = clanId;
        syncCounter(id);
    }

    private static void syncCounter(int id) {
        if (id >= counter) {
            counter = id + 1;
        }
    }

    public int getId()           { return id; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getEmail() {return email;}
    public Role getRole() { return role; }

    public Clan getClan_id() {return clan_id;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "id->[" + id + "] " + username + " (" + role + ")";
    }
}
