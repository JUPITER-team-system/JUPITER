package com.management.jupiter.services;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;

public class AdminService {

    private final UserService          userService;
    private final AdminRepositoryImpl  adminRepository;

    public AdminService(UserService userService, AdminRepositoryImpl adminRepository) {
        this.userService     = userService;
        this.adminRepository = adminRepository;
    }

    /**
     * Crea un nuevo usuario.
     * - ADMIN : sin clan (null).
     * - TL    : clan null por defecto; se asigna después desde la vista de Clanes.
     * - CODER : clan null por defecto; ídem.
     */
    public User createUser(String username, String email, String password,
                           Role role, Clan clan, TlType tlType) throws Exception {

        if (username == null || username.isBlank() ||
            email    == null || email.isBlank()    ||
            password == null || password.isBlank() ||
            role     == null) {
            throw new Exception("Name, email, password and role are required.");
        }

        if (userService.emailExists(email.trim())) {
            throw new Exception("Email already exists: " + email.trim());
        }

        User user;
        switch (role) {
            case ADMIN -> user = new Admin(
                    null,                          // id lo genera la BD
                    username.trim(), email.trim(), password.trim(), role);
            case TL -> {
                TlType type = (tlType != null) ? tlType : TlType.PROGRAMACION;
                user = new Tl(null, username.trim(), email.trim(), password.trim(), role, type);
            }
            case CODER -> user = new Coder(
                    null, username.trim(), email.trim(), password.trim(), role);
            default -> throw new Exception("Unknown role: " + role);
        }

        adminRepository.save(user);
        return user;
    }

    public void deleteUser(String value) {
        try {
            userService.deleteUser(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Actualiza un campo específico de un usuario.
     * @param idOrEmail  UUID o email del usuario.
     * @param newValue   Nuevo valor.
     * @param fieldName  "full_name" | "email" | "password"
     */
    public static void updateUser(String idOrEmail, String newValue, String fieldName) {
        new AdminRepositoryImpl().updateField(idOrEmail, newValue, fieldName);
    }
}
