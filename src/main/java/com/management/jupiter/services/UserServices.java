package com.management.jupiter.services;

import com.management.jupiter.models.User;
import com.management.jupiter.repository.UserRepository;

public class UserServices {
    public static User LoginService(String email, String password) throws Exception {
        User user = UserRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("Some of the fields do not match");
        }

        if (user.getPassword().trim().equals(password)) {
            return user;
        }

        throw new Exception("Some of the fields do not match");

    }
}
