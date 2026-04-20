package com.management.jupiter.controllers;

import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.*;
import com.management.jupiter.services.AdminService;

public class AdminController {

    private final AdminService service;

    public AdminController (AdminService service) {

        this.service = service;

    }

    public void createUser(String username, String email, String password, Role role, Clan clan ,TlType tlType) {

        try {

            User createdUser = service.createUser(username, email, password, role, clan ,tlType);
            System.out.println("User created successfully: " + createdUser);

        } catch (IllegalArgumentException e) {

            System.out.println("Invalid role or clan");

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
    }

    public void deleteUser(String value) {

        service.deleteUser(value);

    }

    public void updateUser(String idOrEmail, String newValue, String fieldName) {

        if (idOrEmail == null || newValue == null || fieldName == null) {

            throw new RuntimeException("All fields are required");

        }

        service.updateUser(idOrEmail, newValue, fieldName);

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
