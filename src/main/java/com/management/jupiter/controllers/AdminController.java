package com.management.jupiter.controllers;

import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.services.AdminService;
import com.management.jupiter.services.UserServices;
import com.management.jupiter.ui.admin.UpdateUserView;
import com.management.jupiter.views.InputView;

import java.util.Scanner;

public class AdminController {
    public static Scanner scanner = InputView.getScanner();

    public static void createUser() {
        try {
            System.out.println("===== CREATE USER =====");
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Role (ADMIN, TL, CODER): ");
            Role role = Role.valueOf(scanner.nextLine().trim().toUpperCase());

            Clan clan = null;
            if (role == Role.TL || role == Role.CODER) {
                System.out.print("Clan (HAMILTON, THOMPSON, TESLA, NAKAMOTO): ");
                clan = Clan.valueOf(scanner.nextLine().trim().toUpperCase());
            }
            TlType tlType = null;
            if (role == Role.TL) {
                System.out.print("TL type (PROGRAMACION, INGLES): ");
                tlType = TlType.valueOf(scanner.nextLine().trim().toUpperCase());
            }

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
        AdminService.deleteUser(value);
    }

    public static void updateUser(String idOrEmail, String newValue, String fieldName) {
        if (idOrEmail == null || newValue == null || fieldName == null){
            throw new RuntimeException("All fields are required");
        }
        AdminService.updateUser(idOrEmail, newValue, fieldName);

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
