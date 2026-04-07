package com.management.jupiter.controllers;

import com.management.jupiter.models.Attempts;
import com.management.jupiter.models.User;
import com.management.jupiter.services.UserServices;
import com.management.jupiter.views.LoginView;

import java.util.HashMap;
import java.util.Map;

public class UserController {
    private static final int MAX_ATTEMPTS = 3;
    private static final int SECONDS_BLOCK = 30;

    // State per user (email)
    private static final Map<String, Attempts> attemptsPerUser = new HashMap<>();

    public static User login() {
        LoginView loginView = new LoginView();

        while (true) {
            LoginView.LoginRequest request = loginView.promptCredentials();
            String email = request.email();

            // get or create user state
            Attempts attempts = attemptsPerUser.getOrDefault(email, new Attempts());

            // check if is blocked
            if (System.currentTimeMillis() < attempts.blockedUntil) {
                long secondsRemaining = (attempts.blockedUntil - System.currentTimeMillis()) / 1000;
                loginView.showBlocked(secondsRemaining);
                if (!loginView.askRetry()) {
                    return null;
                }
                continue;
            }

            try {
                User loggedUser = UserServices.LoginService(request.email(), request.password());
                attempts.reset();
                attemptsPerUser.put(email, attempts);
                loginView.showLoginSuccess(loggedUser);
                return loggedUser;
            } catch (Exception e) {
                loginView.showLoginError(e.getMessage());
            }

            // Login failed
            attempts.increase();

            if (attempts.failedAttempts >= MAX_ATTEMPTS) {
                attempts.block(SECONDS_BLOCK);
                loginView.showBlockedForSeconds(SECONDS_BLOCK);
            }

            attemptsPerUser.put(email, attempts);

            if (!loginView.askRetry()) {
                return null;
            }
        }
    }

}
