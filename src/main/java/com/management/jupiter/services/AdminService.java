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
import com.management.jupiter.impl.UserRepositoryImpl;

import java.util.List;

public class AdminService {
    private static AdminRepository adminRepository = new AdminRepository();

    public static User createUser(String username, String email, String password, Role role, Clan clan, TlType tlType) throws Exception {
        // Validación de campos obligatorios
        if (username == null || username.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank() ||
                role == null) {
            throw new Exception("All fields are required");
        }

        // Verificar si el email ya existe usando UserRepositoryImpl
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            throw new Exception("Email already exists");
        }

        User user;
        // Generar ID simple (temporal hasta tener un sistema de IDs robusto)
        int nextId = (int) (System.currentTimeMillis() % 10000);
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

        // Guardar usuario usando UserRepositoryImpl en lugar de AdminRepository
        userRepository.save(user);
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
