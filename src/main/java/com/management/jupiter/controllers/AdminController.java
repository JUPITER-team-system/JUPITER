package com.management.jupiter.controllers;

import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.UserRepository;
import com.management.jupiter.services.AdminService;
import com.management.jupiter.services.UserServices;
import com.management.jupiter.ui.admin.UpdateUserView;
import com.management.jupiter.views.InputView;

import java.util.Scanner;

public class AdminController {
    public static Scanner scanner = InputView.getScanner();

    public static void createUser(String username, String email, String password, Role role, TlType tlType) {
        try {
            if (username == null || username.isBlank() ||
                    email == null || email.isBlank() ||
                    password == null || password.isBlank() ||
                    role == null) {
                throw new Exception("All fields are required");
            }

            if (UserRepository.findByIdOrEmail(email.trim()) != null) {
                throw new Exception("Email already exists");
            }

//            System.out.println("===== CREATE USER =====");
//            System.out.print("Username: ");
//            String username = scanner.nextLine();

//            System.out.print("Email: ");
//            String email = scanner.nextLine();

//            System.out.print("Password: ");
//            String password = scanner.nextLine();

//            System.out.print("Role (ADMIN, TL, CODER): ");
//            Role role = Role.valueOf(scanner.nextLine().trim().toUpperCase());

//            TlType tlType = null;
//            if (role == Role.TL) {
//                System.out.print("TL type (PROGRAMACION, INGLES): ");
//                tlType = TlType.valueOf(scanner.nextLine().trim().toUpperCase());
//            }

            User createdUser = AdminService.createUser(username, email, password, role, tlType);
            System.out.println("User created successfully: " + createdUser);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role or clan");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteUser() {
        System.out.println("Ingresa el id o email del usuario que deseas eliminar");
        String value = scanner.nextLine();
        AdminService.deleteUser(value);
    }

    public static void updateUser(String idOrEmail, String newValue, String fieldName) {
        if (idOrEmail == null || newValue == null || fieldName == null) {
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
