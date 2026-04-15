package com.management.jupiter.services;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.AdminRepository;
import com.management.jupiter.repository.UserRepository;

import java.util.List;

public class AdminService {
    private static final AdminRepository adminRepository = new AdminRepository();

    public static User createUser(String username, String email, String password, Role role, TlType tlType) throws Exception {

        User user;
        int nextId = UserRepository.nextId();
        if (role == Role.ADMIN) {
            user = new Admin(nextId, username.trim(), email.trim(), password.trim(), role);
        } else {
            if (role == Role.TL) {
                user = new Tl(nextId, username.trim(), email.trim(), password.trim(), role, tlType);
            } else {
                user = new Coder(nextId, username.trim(), email.trim(), password.trim(), role);
            }
        }

        AdminRepository.save(user);
        return user;
    }

    public static void getUsersByRol(String role) {
        var handler = new Handler();
        List<String[]> users = handler.read("users.csv");
        users.stream()
                .filter(user -> user.length > 0 && user[4].equals(role))
                .forEach(user -> {
                    System.out.println("Name: " + user[1] + " Email: " + user[2] + " Rol: " + user[4]);
                });
    }

    public static void deleteUser(String value) {
        try {
            adminRepository.deleteUser(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateUser(String idOrEmail, String newValue, String fieldName) {
        adminRepository.updateUser(idOrEmail, newValue, fieldName);
    }
}
