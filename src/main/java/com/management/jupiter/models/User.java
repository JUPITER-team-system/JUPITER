//Place where you create the class of User.
package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;

//Place where you create the class of User.
public class User {
    private static int counter = 1;

    private final int id;
    private final String username;
    private final String email;
    private final String password;
    private final Role role;

    public User(String username, String email, String password, Role role) {
        this.id = counter++;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
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
