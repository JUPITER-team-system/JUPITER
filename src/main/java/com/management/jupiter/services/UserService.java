package com.management.jupiter.services;

import com.management.jupiter.models.User;
import com.management.jupiter.repository.interfaces.UserRepository;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;
import com.management.jupiter.security.PasswordHasher;


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

        // Smart password validation - detects if password is hashed or plain text
        String storedPassword = foundUser.getPassword();
        String inputPassword = password.trim();
        
        if (storedPassword == null || storedPassword.isEmpty()) {
            throw new Exception("Incorrect password");
        }
        
        // Trim stored password to handle any trailing whitespace from DB
        String storedTrimmed = storedPassword.trim();
        
        if (isHashedPassword(storedTrimmed)) {
            // Password is hashed, use BCrypt check
            if (!PasswordHasher.check(inputPassword, storedTrimmed)) {
                throw new Exception("Incorrect password");
            }
        } else {
            // Password is plain text, compare directly
            if (!inputPassword.equals(storedTrimmed)) {
                throw new Exception("Incorrect password");
            }
        }

        return foundUser;
    }
    
    /**
     * Detects if a password is hashed with BCrypt
     * BCrypt hashes start with $2a$, $2b$, $2y$, or $2x$
     */
    private boolean isHashedPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // BCrypt hashes have the format: $2a$10$... or $2b$12$... etc
        return password.startsWith("$2a$") || 
               password.startsWith("$2b$") || 
               password.startsWith("$2y$") || 
               password.startsWith("$2x$");
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
