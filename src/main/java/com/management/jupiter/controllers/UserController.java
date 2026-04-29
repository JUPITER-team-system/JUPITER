package com.management.jupiter.controllers;

import com.management.jupiter.exceptions.UserBlockedException;
import com.management.jupiter.models.*;
import com.management.jupiter.services.UserService;

import java.util.*;

public class UserController {

    private static final int MAX_ATTEMPTS = 3;
    private static final int SECONDS_BLOCK = 30;
    private final UserService userService;

    // State per user (email)
    private final Map<String, Attempts> attemptsPerUser = new HashMap<>();

    public UserController() {
        this(new UserService());
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User login(String email, String password) throws UserBlockedException, Exception {

        Attempts attempts = attemptsPerUser.getOrDefault(email, new Attempts());

        if (System.currentTimeMillis() < attempts.blockedUntil){
            throw new UserBlockedException(attempts.blockedUntil);
        }

        try {

            User loggedUser = userService.authenticate(email, password);
            attempts.reset();
            attemptsPerUser.put(email, attempts);
            return loggedUser;

        }catch (Exception err){

            attempts.increase();

            if (attempts.failedAttempts >= MAX_ATTEMPTS){
                attempts.block(SECONDS_BLOCK);
                attemptsPerUser.put(email, attempts);
                throw new UserBlockedException(attempts.blockedUntil);
            }

            attemptsPerUser.put(email, attempts);
            throw new Exception("Invalid credentials");

        }

    }

    public int getLeftAttempts(String email) {

        Attempts attempts = attemptsPerUser.get(email);
        return (attempts == null) ? MAX_ATTEMPTS : (MAX_ATTEMPTS - attempts.failedAttempts);

    }

}
