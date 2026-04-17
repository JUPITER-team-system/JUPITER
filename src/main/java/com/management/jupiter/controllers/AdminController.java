package com.management.jupiter.controllers;

import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.services.AdminService;
import com.management.jupiter.services.UserService;
import com.management.jupiter.ui.admin.UpdateUserView;
import com.management.jupiter.views.InputView;

import java.util.Scanner;

public class AdminController {
    public static Scanner scanner = InputView.getScanner();

    public static void createUser(String username, String email, String password, Role role, Clan clan, TlType tlType) {
        try {
            User createdUser = AdminService.createUser(username, email, password, role, clan, tlType);
            System.out.println("User created successfully: " + createdUser);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role or clan");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteUser() {
        System.out.println("Enter the email or id of the user you want to delete");
        String value = scanner.nextLine();
        try {
            AdminService.deleteUser(value);
            System.out.println("User deleted successfully");
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    public static void updateUser(String idOrEmail, String newValue, String fieldName) {
        if (idOrEmail == null || newValue == null || fieldName == null) {
            System.out.println("All fields are required");
            return;
        }
        
        try {
            AdminService.updateUser(idOrEmail, newValue, fieldName);
            System.out.println("User updated successfully");
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }
//    public static void getUsersByRol(){
//        Scanner scanner = InputView.getScanner();
//        try{
//            System.out.println("Filtrar por:");
//            System.out.println("1.Admins");
//            System.out.println("2.Coders");
//            System.out.println("3.TLs");
//            System.out.println("0.Salir");
//
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
}
