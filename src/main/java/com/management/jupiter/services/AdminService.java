package com.management.jupiter.services;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.repository.AdminRepository;
import com.management.jupiter.repository.UserRepository;

public class AdminService {
    public static User createUser(String username, String email, String password, Role role, Clan clan) throws Exception {
        if (username == null || username.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank() ||
                role == null) {
            throw new Exception("All fields are required");
        }

        if (UserRepository.findByEmail(email.trim()) != null) {
            throw new Exception("Email already exists");
        }

        User user;
        if (role == Role.ADMIN) {
            user = new Admin(username.trim(), email.trim(), password.trim(), role);
        } else {
            if (clan == null) {
                throw new Exception("Clan is required for TL and CODER");
            }

            if (role == Role.TL) {
                user = new Tl(username.trim(), email.trim(), password.trim(), role, clan);
            } else {
                user = new Coder(username.trim(), email.trim(), password.trim(), role, clan);
            }
        }

        AdminRepository.save(user);
        return user;
    }
}