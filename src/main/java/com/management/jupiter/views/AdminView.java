package com.management.jupiter.views;

import com.management.jupiter.controllers.AdminController;
import com.management.jupiter.controllers.ClanController;
import com.management.jupiter.models.Admin;
import com.management.jupiter.models.enums.*;
import com.management.jupiter.security.interfaces.LoginSession;
import com.management.jupiter.ui.users.AdminUI;
import com.management.jupiter.util.scanner.ScannerUtil;

public class AdminView {

    private final ScannerUtil input;
    private final LoginSession session;
    private final AdminController adminController;
    private final ClanController clanController;


    public AdminView (ScannerUtil input,
                      LoginSession session,
                      AdminController adminController,
                      ClanController clanController) {

        this.input = input;
        this.session = session;
        this.adminController = adminController;
        this.clanController = clanController;
    }

    public void show (Admin admin) {

        AdminUI.admin(admin);
        AdminUI.adminDec();

        int dec;

        do {

            dec = input.readInt("Which is your decision?: ");

            switch (dec) {
                case 1:
                    //Add Soon...
                    break;
                case 2:
                    //Add Soon...
                    break;
                case 3:
                    //Add Soon...
                    break;
                case  4:
                    clanManagement(admin);
                    break;
                case 5:
                    userManagement(admin);
                    break;
            }

        } while (dec != 0);

    }

    public void clanManagement(Admin admin){

        AdminUI.admin(admin);
        AdminUI.clanManage();

        int dec;

        do {

            dec = input.readInt("Which is your decision?: ");

            switch (dec) {
                case 1:
                    //Add Soon...
                    break;
                case 2:
                    //Add Soon...
                    break;
                case 3:
                    //Add Soon...
                    break;
                case  4:
                    //Add Soon...
                    break;
                case 5:
                    //Add Soon...
                    break;
                case 6:
                    //Add Soon...
                    break;
            }

        } while (dec != 0);

    }

    public void userManagement(Admin admin){

        AdminUI.admin(admin);
        AdminUI.userManage();

        int dec;

        do {

            dec = input.readInt("Which is your decision?: ");

            switch (dec) {

                case 1:
                    addUser();
                    break;
                case 2:
                    deleteUser();
                    break;
                case 3:
                    editUser();
                    break;

            }

        } while (dec != 0);

    }

    private void addUser() {

        while (true) {

            String name = input.readString("What's his/her name? (or 'exit' to quit): ");
            if (name.equalsIgnoreCase("exit")) break;

            String email = input.readString("What's his/her email?: ");
            String password = input.readString("What's his/her password?: ");

            Role role;

            try{

                role = Role.valueOf(input.readString("what's her/his role? (Coder/Tl): ").toUpperCase());

            }catch (IllegalArgumentException err) {

                System.out.println("Invalid type");
                continue;

            }

            TlType tl = null;

            if(role == Role.TL) {

                try {

                    tl = TlType.valueOf(input.readString("TL Type (PROGRAMACION/INGLES): ").toUpperCase());

                } catch (IllegalArgumentException err) {

                    System.out.println("Invalid type");
                    continue;

                }
            }

            adminController.createUser(name, email, password, role, null ,tl);

        }

    }

    private void deleteUser () {

        String value = input.readString("Which is her/his email or id: ");

        if (value.equals(session.loggedUser().getId()) || value.equals(session.loggedUser().getEmail())){

            System.out.println("You can't delete yourself!");
            return;

        }

        adminController.deleteUser(value);

    }

    private void editUser () {

        String idOrEmail = input.readString("Enter the user id or email: ");

        if (idOrEmail.equals(session.loggedUser().getId()) || idOrEmail.equals(session.loggedUser().getEmail())){

            System.out.println("You can't modify yourself!");
            return;

        }

        AdminUI.userOptions();

        int dec = input.readInt("What's your decision?: ");
        String fieldName = "";

        switch (dec) {

            case 1 -> fieldName = "name";
            case 2 -> fieldName = "email";
            case 3 -> fieldName = "password";
            case 4 -> fieldName = "role";
            case 5 -> {
                System.out.println("Operation Cancelled");
                return;
            }
            default -> {
                System.out.println("Invalid option");
                return;
            }

        }

        String newValue = input.readString("Enter the new value of " + fieldName + ": ");

        try {

            adminController.updateUser(idOrEmail, newValue, fieldName);
            System.out.println("User updated correctly");

        }catch (RuntimeException err){

            System.err.println("Error to update user" + err.getMessage());

        }


    }
}