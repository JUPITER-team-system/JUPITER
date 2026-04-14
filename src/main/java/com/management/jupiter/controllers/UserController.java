package com.management.jupiter.controllers;

import com.management.jupiter.exceptions.UserBlockedException;
import com.management.jupiter.models.Attempts;
import com.management.jupiter.models.User;
import com.management.jupiter.services.UserServices;

import java.util.HashMap;
import java.util.Map;

public class UserController {

    private static final int MAX_ATTEMPTS = 3;
    private static final int SECONDS_BLOCK = 30;

    // State per user (email)
    private final Map<String, Attempts> attemptsPerUser = new HashMap<>();

    public User login(String email, String password) throws UserBlockedException, Exception {

        Attempts attempts = attemptsPerUser.getOrDefault(email, new Attempts());

        if (System.currentTimeMillis() < attempts.blockedUntil){
            throw new UserBlockedException(attempts.blockedUntil);
        }

        try {

            User loggedUser = UserServices.LoginService(email, password);
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



}
