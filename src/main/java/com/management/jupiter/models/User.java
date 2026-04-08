package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;

public class User {
    private static int counter = 1;

    private final int    id;
    private final String username;
    private final String email;
    private final String password;
    private final Role   role;

    public User(String username, String email, String password, Role role) {
        this.id       = counter++;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public User(int id, String username, String email, String password, Role role) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
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

    @Override
    public String toString() {
        return "id->[" + id + "] " + username + " (" + role + ")";
    }
}
