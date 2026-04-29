package com.management.jupiter.services;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.interfaces.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminService {

    private final UserService userService;
    private final UserRepository adminRepository;

    public AdminService (UserService userService, UserRepository adminRepository){
        this.adminRepository = adminRepository;
        this.userService = userService;
    }

    public List<User> getAll() {

        List<User> usersList = new ArrayList<>();

        try {

            usersList = adminRepository.getAll();

        } catch (Exception err) {

            System.err.println("Error to obtain Users: " + err.getMessage());

        }

        return usersList;

    }

    // I bring the entire NewUser object
    public User createUser(String username, String email, String password, Role role, Clan clan, TlType tlType) throws Exception {

        // Check if email already exists using UserService
        if (userService.emailExists(email.trim())) {
            throw new Exception("Email already exists");
        }

        User user;
        // ID will be assigned by DB (UUID auto-generated); we pass a temporary placeholder
        String tempId = java.util.UUID.randomUUID().toString();

        if (role == Role.ADMIN) {
            // Admin uses the base User constructor without clan
            user = new Admin(tempId, username.trim(), email.trim(), password.trim(), role);
        } else {
            if (clan == null) {
                throw new Exception("Clan is required for TL and CODER");
            }

            if (role == Role.TL) {
                user = new TlWithClan(tempId, username.trim(), email.trim(), password.trim(), role,
                        tlType != null ? tlType : TlType.PROGRAMACION, clan);
            } else {
                // For CODER: use a User that carries the clan so getClan_id() works in save()
                user = new User(tempId, username.trim(), email.trim(), password.trim(), role, clan);
            }
        }

        // Save user using UserRepositoryImpl
        adminRepository.save(user);
        return user;
    }

    //Show the users exist in to database
    @Deprecated
    public void getUsersByRol(String role) {
        var handler = new Handler();
        List<String[]> users = handler.read("users.csv");
        users.stream()
                .filter(user -> user.length > 0 && user[4].equals(role))
                .forEach(user -> {
                    System.out.println("Name: " + user[1] + " Email: " + user[2] + " Role: " + user[4]);
                });
    }

    public void deleteUser(String value) {
        try {
            userService.deleteUser(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void updateUser(String idOrEmail, String newValue, String fieldName) {
        if (idOrEmail == null || newValue == null || fieldName == null) {
            throw new IllegalArgumentException("All fields are required");
        }
        
        try {
            // Find the user by ID or email
            Optional<User> userOpt = adminRepository.findById(idOrEmail);
            if (userOpt.isEmpty()) {
                // Try to find by email if ID search failed
                userOpt = adminRepository.findByEmail(idOrEmail);
            }
            
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("User not found: " + idOrEmail);
            }
            
            User user = userOpt.get();
            
            // Create a new user object with updated values
            User updatedUser;
            Clan clanToUpdate = user.getClan_id();
            
            switch (fieldName.toLowerCase()) {
                case "username":
                    updatedUser = new User(user.getId(), newValue, user.getEmail(), user.getPassword(), user.getRole(), clanToUpdate);
                    break;
                case "email":
                    updatedUser = new User(user.getId(), user.getUsername(), newValue, user.getPassword(), user.getRole(), clanToUpdate);
                    break;
                case "role":
                    Role newRole = Role.valueOf(newValue.toUpperCase());
                    updatedUser = new User(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), newRole, clanToUpdate);
                    break;
                case "clan_id":
                    // Handle clan assignment - lookup clan from service
                    if (newValue == null || newValue.trim().isEmpty()) {
                        clanToUpdate = null;
                    } else {
                        // Try to load the actual clan from DB using its ID
                        try {
                            com.management.jupiter.services.ClanService clanSvc = new com.management.jupiter.services.ClanService(new com.management.jupiter.repository.impl.ClanRepositoryImpl());
                            java.util.Optional<com.management.jupiter.models.Clan> clanOpt = clanSvc.readIdOrName(newValue.trim());
                            clanToUpdate = clanOpt.orElse(null);
                        } catch (Exception ex) {
                            System.err.println("[WARN] Could not load clan for update: " + ex.getMessage());
                            clanToUpdate = new Clan(newValue.trim(), "", "");
                        }
                    }
                    updatedUser = new User(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRole(), clanToUpdate);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field name: " + fieldName);
            }
            
            // Update the user in the database
            adminRepository.update(updatedUser);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public Optional<User> findById (String id){

        if (id == null || id.isBlank()){

            throw new IllegalArgumentException("The Id is required");

        }

        return adminRepository.findById(id);

    }

    /**
     * A Tl subclass that overrides getClan_id() so that AdminRepositoryImpl.save()
     * can persist the clan_id correctly. Tl normally passes null to super() for clan.
     */
    private static class TlWithClan extends Tl {
        private final Clan clan;

        public TlWithClan(String id, String username, String email, String password,
                          Role role, TlType tlType, Clan clan) {
            super(id, username, email, password, role, tlType);
            this.clan = clan;
            if (clan != null) {
                addClan(clan);
            }
        }

        @Override
        public Clan getClan_id() {
            return clan;
        }
    }
}
