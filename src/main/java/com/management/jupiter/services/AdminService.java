package com.management.jupiter.services;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.AdminRepository;
import com.management.jupiter.repository.UserRepository;

public class AdminService {
    public static User createUser(String username, String email, String password, Role role, Clan clan, TlType tlType) throws Exception {
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
        int nextId = UserRepository.nextId();
        if (role == Role.ADMIN) {
            user = new Admin(nextId, username.trim(), email.trim(), password.trim(), role);
        } else {
            if (clan == null) {
                throw new Exception("Clan is required for TL and CODER");
            }

            if (role == Role.TL) {
                user = new Tl(nextId, username.trim(), email.trim(), password.trim(), role, tlType);
            } else {
                user = new Coder(nextId, username.trim(), email.trim(), password.trim(), role);
            }
        }

        AdminRepository.save(user);
        return user;
    }
}
