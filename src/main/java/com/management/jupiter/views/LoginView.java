package com.management.jupiter.views;

import com.management.jupiter.controllers.UserController;
import com.management.jupiter.exceptions.UserBlockedException;
import com.management.jupiter.models.User;
import com.management.jupiter.ui.auth.LoginUI;
import com.management.jupiter.util.scanner.ScannerUtil;

public class LoginView {

    private final ScannerUtil input;
    private final UserController controller;

    public LoginView(ScannerUtil input, UserController controller ){
        this.input = input;
        this.controller = controller;
    }

    public User login () {

        int attempts = 3;

        while (true){

            LoginUI.login(attempts);

            String email = input.readString("Email: ");
            String password = input.readString("Password: ");

            try{

                User user = controller.login(email, password);

            }catch (UserBlockedException err){

                System.out.println("You are blocked by " + err.remaningTime() + " seconds");

            }catch (Exception err){

                System.err.println("\n Error: " + err.getMessage());
                attempts = controller.getLeftAttempts(email);

            }
        }

    }
}