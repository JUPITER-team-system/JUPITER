package com.management.jupiter.views;

import com.management.jupiter.controllers.AdminController;
import com.management.jupiter.models.Admin;
import com.management.jupiter.models.enums.*;
import com.management.jupiter.ui.users.AdminUI;
import com.management.jupiter.util.scanner.ScannerUtil;

public class AdminView {

    private final ScannerUtil input;
    private final AdminController controller;

    public AdminView (ScannerUtil input, AdminController controller) {
        this.input = input;
        this.controller = controller;
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
                    //Add Soon...
                    break;
                case 3:
                    //Add Soon...
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

            controller.createUser(name, email, password, role, tl);

        }

    }

}