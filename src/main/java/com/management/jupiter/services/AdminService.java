package com.management.jupiter.services;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
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
        // Generate simple ID (temporary until robust ID system is implemented)
        String nextId = String.valueOf(System.currentTimeMillis() % 10000);
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
        // TODO: Implement update method using UserRepository
        // This method needs to be implemented in the new architecture
        throw new UnsupportedOperationException("Update user method not yet implemented in new architecture");
    }

    public Optional<User> findById (String id){

        if (id == null || id.isBlank()){

            throw new IllegalArgumentException("The Id is required");

        }

        return adminRepository.findById(id);

    }
}
