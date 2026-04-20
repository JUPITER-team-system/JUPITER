package com.management.jupiter.services;

import com.management.jupiter.models.User;
import com.management.jupiter.repository.interfaces.UserRepository;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;


import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    // Singleton instance for static methods compatibility
    private static final UserService instance = new UserService();

    public UserService() {
        this.userRepository = new AdminRepositoryImpl();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Searc User by email
    public Optional<User> findByEmailOrId(String emailOrId) {
        if (emailOrId == null || emailOrId.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmedValue = emailOrId.trim();

        // Try to parse as ID first - check if it looks like a UUID
        try {
            // If it contains hyphens, treat as UUID ID
            if (trimmedValue.contains("-")) {
                return userRepository.findById(trimmedValue);
            } else {
                // Try parsing as email
                return userRepository.findByEmail(trimmedValue);
            }
        } catch (Exception e) {
            // If not a number, treat as email
            return userRepository.findByEmail(trimmedValue);
        }
    }


    public User authenticate(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password is required");
        }

        Optional<User> userOpt = userRepository.findByEmail(email.trim());

        if (userOpt.isEmpty()) {
            throw new Exception("User not found in the system");
        }

        User foundUser = userOpt.get();

        if (!foundUser.getPassword().equals(password.trim())) {
            throw new Exception("Incorrect password");
        }

        return foundUser;
    }

    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return userRepository.findByEmail(email.trim()).isPresent();
    }


    public void deleteUser(String emailOrId) throws Exception {
        Optional<User> userOpt = findByEmailOrId(emailOrId);

        if (userOpt.isEmpty()) {
            throw new Exception("User not found: " + emailOrId);
        }

        userRepository.delete(userOpt.get().getId());
    }

    // ===== STATIC METHODS FOR COMPATIBILITY =====


    public static User LoginService(String email, String password) throws Exception {
        return instance.authenticate(email, password);
    }
}
