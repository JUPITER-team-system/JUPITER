package com.management.jupiter.views;

import com.management.jupiter.models.User;

import java.util.Scanner;

public class LoginView {
    private final Scanner scanner;

    public LoginView() {
        this.scanner = InputView.getScanner();
    }

    public LoginRequest promptCredentials() {
        System.out.println("========= LOGIN ==========");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        return new LoginRequest(email, password);
    }

    public void showLoginSuccess(User user) {
        System.out.println("Login success! " + user);
    }

    public void showLoginError(String message) {
        System.out.println(message);
    }

    public void showBlocked(long secondsRemaining) {
        System.out.println("User blocked. Try again in " + secondsRemaining + " seconds.");
    }

    public void showBlockedForSeconds(int seconds) {
        System.out.println("Many attempts. User blocked for " + seconds + " seconds.");
    }

    public boolean askRetry() {
        while (true) {
            System.out.println("1. Try again");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            String option = scanner.nextLine().trim();

            if ("1".equals(option)) {
                return true;
            }
            if ("0".equals(option)) {
                System.out.println("Exit ...");
                return false;
            }
            System.out.println("Incorrect option ...");
        }
    }

    public void closeScanner(){
    }

    public record LoginRequest(String email, String password) {
    }
}
