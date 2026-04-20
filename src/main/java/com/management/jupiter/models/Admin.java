package com.management.jupiter.models;

import com.management.jupiter.models.enums.Role;

//Place where you create the class of Admin.
public class Admin extends User {

    public Admin(String username,String email, String password, Role role){
        super(username, email, password, role, null);
    }

    public Admin(String id, String username,String email, String password, Role role){
        super(id, username, email, password, role, null);
    }

}
