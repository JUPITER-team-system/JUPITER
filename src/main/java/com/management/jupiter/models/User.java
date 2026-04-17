package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;

import java.util.Objects;

public class User {
    private static int counter = 1;

    private String id;
    private final String username;
    private final String email;
    private final String password;
    private final Role   role;

    public User(String username, String email, String password, Role role) {
        this.id  = null;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public User(String id, String username, String email, String password, Role role) {
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public String getId() { return id; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getEmail() {return email;}
    public Role getRole() { return role; }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return Objects.equals(id, user.id);
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
