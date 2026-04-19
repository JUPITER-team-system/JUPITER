package com.management.jupiter.services;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.AdminRepository;
import com.management.jupiter.repository.UserRepository;

import java.util.List;

public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService (AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public  User createUser(String username, String email, String password, Role role, TlType tlType) throws Exception {
        if (username == null || username.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank() ||
                role == null) {
            throw new Exception("All fields are required");
        }

        if (UserRepository.findByIdOrEmail(email.trim()) != null) {
            throw new Exception("Email already exists");
        }
        User user;
        String  nextId = String.valueOf(UserRepository.nextId());
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

    public void getUsersByRol(String role) {
        var handler = new Handler();
        List<String[]> users = handler.read("users.csv");
        users.stream()
                .filter(user -> user.length > 0 && user[4].equals(role))
                .forEach(user -> {
                    System.out.println("Name: " + user[1] + " Email: " + user[2] + " Rol: " + user[4]);
                });
    }

    public void deleteUser(String value) {
        try {
            adminRepository.deleteUser(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUser(String idOrEmail, String newValue, String fieldName) {
        adminRepository.updateUser(idOrEmail, newValue, fieldName);
    }
}
