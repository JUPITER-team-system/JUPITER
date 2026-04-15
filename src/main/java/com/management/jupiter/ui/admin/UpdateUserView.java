package com.management.jupiter.ui.admin;

import static com.management.jupiter.controllers.AdminController.scanner;

public class UpdateUserView {

    public UpdateRequest updateUserSubmenu() {
        System.out.print("Ingresa id o email: ");
        String idOrEmail = scanner.nextLine();

        System.out.println("Campo a actualizar:");
        System.out.println("1. name");
        System.out.println("2. email");
        System.out.println("3. password");
        System.out.println("4. role");
        String option = scanner.nextLine();

        String fieldName = switch (option) {
            case "1" -> "name";
            case "2" -> "email";
            case "3" -> "password";
            case "4" -> "role";
            default -> null;
        };

        if (fieldName == null) {
            System.out.println("Opción inválida");
            return null;
        }

        System.out.println("Ingresa el nuevo valor para -> " + fieldName);
        String newValue = scanner.nextLine();
        return new UpdateRequest(idOrEmail, newValue, fieldName);
    }

    public record UpdateRequest(String idOrEmail, String newValue, String fieldName) {
    }
}

