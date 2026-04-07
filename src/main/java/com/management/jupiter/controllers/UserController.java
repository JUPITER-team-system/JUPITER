package com.management.jupiter.controllers;

import com.management.jupiter.models.Attempts;
import com.management.jupiter.models.User;
import com.management.jupiter.services.UserServices;
import com.management.jupiter.views.InputView;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserController {
    private static final int MAX_ATTEMPTS = 3;
    private static final int SECONDS_BLOCK = 30;

    // State per user (email)
    private static final Map<String, Attempts> attemptsPerUser = new HashMap<>();

    public static User LoginController() throws Exception {
        Scanner scanner = InputView.getScanner();
        System.out.println("=== LOGIN JUPITER ===");

        User loggedUser = null;

        while (true) {
            System.out.print("Email: ");
            String email = scanner.nextLine();

            // get or create user state
            Attempts attempts = attemptsPerUser.getOrDefault(email, new Attempts());

            // check if is blocked
            if (System.currentTimeMillis() < attempts.blockedUntil) {
                long secondsRemaining = (attempts.blockedUntil - System.currentTimeMillis()) / 1000;
                System.out.println("User blocked. Try again in " + secondsRemaining + " seconds.");
                continue;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                loggedUser = UserServices.LoginService(email, password);

                if (loggedUser != null) {
                    System.out.println("\nAccess Successfully. " + loggedUser);

                    // reset attempts
                    attempts.reset();
                    attemptsPerUser.put(email, attempts);
                    return loggedUser;
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            // Login failed
            attempts.increase();

            if (attempts.failedAttempts >= MAX_ATTEMPTS) {
                attempts.block(SECONDS_BLOCK);
                System.out.println("Many attempts. User blocked for 30 seconds.");
            }

            attemptsPerUser.put(email, attempts);
        }
    }

}
