package com.management.jupiter.services;

import com.management.jupiter.models.User;
import com.management.jupiter.repository.interfaces.UserRepository;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;
import com.management.jupiter.security.PasswordHasher;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    // Singleton para compatibilidad con llamadas estáticas
    private static final UserService instance = new UserService();

    public UserService() {
        this.userRepository = new AdminRepositoryImpl();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Busca usuario por email o UUID. */
    public Optional<User> findByEmailOrId(String emailOrId) {
        if (emailOrId == null || emailOrId.trim().isEmpty()) {
            return Optional.empty();
        }
        String trimmed = emailOrId.trim();
        try {
            if (trimmed.contains("-")) {
                return userRepository.findById(trimmed);
            } else {
                return userRepository.findByEmail(trimmed);
            }
        } catch (Exception e) {
            return userRepository.findByEmail(trimmed);
        }
    }

    /**
     * Autentica al usuario verificando email y contraseña.
     * Soporta contraseñas BCrypt y texto plano (legacy).
     */
    public User authenticate(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password is required");
        }

        Optional<User> userOpt;
        try {
            userOpt = userRepository.findByEmail(email.trim());
        } catch (Exception e) {
            System.err.println("[Auth] DB error looking up email: " + e.getMessage());
            throw new Exception("Connection error. Check your network.");
        }

        if (userOpt.isEmpty()) {
            throw new Exception("User not found in the system");
        }

        User foundUser = userOpt.get();
        String storedPassword = foundUser.getPassword();

        if (storedPassword == null || storedPassword.isBlank()) {
            throw new Exception("User account has no password configured");
        }

        if (!PasswordHasher.check(password.trim(), storedPassword)) {
            throw new Exception("Incorrect password");
        }

        return foundUser;
    }

    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return userRepository.findByEmail(email.trim()).isPresent();
    }

    public void deleteUser(String emailOrId) throws Exception {
        Optional<User> userOpt = findByEmailOrId(emailOrId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found: " + emailOrId);
        }
        userRepository.delete(userOpt.get().getId());
    }

    // ── Compatibilidad con llamadas estáticas ────────────────────────────────

    public static User LoginService(String email, String password) throws Exception {
        return instance.authenticate(email, password);
    }
}
