package com.management.jupiter.views;
import com.management.jupiter.controllers.AdminController;

import java.util.Scanner;


public class AdminView {

    private final Scanner scanner;

    public AdminView(){
        scanner = InputView.getScanner();
    }

    public void menuAdmin(){
        int option;

        do{
            System.out.println(" ===== MENU ADMIN =====");
            System.out.println("1. View Coders");
            System.out.println("2. View Tls");
            System.out.println("3. View Clanes");
            System.out.println("4. Delete user");
            System.out.println("5. Create user");
            System.out.println("6. Delete clan");
            System.out.println("7. Create clan");
            System.out.println("8. Add user into clan");
            System.out.println("0. Exit");
            System.out.println("Select a option");

            String optionInput = scanner.nextLine();
            try {
                option = Integer.parseInt(optionInput);
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option){
                case 1:
                    System.out.println("VIEW CODERS");
                    break;
                case 2:
                    System.out.println("VIEW TLS");
                    break;
                case 3:
                    System.out.println("VIEW CLANES");
                    break;
                case 4:
                    System.out.println("DELETE USER");
                    break;
                case 5:
                    AdminController.createUser();
                    break;
                case 6:
                    System.out.println("DELETE CLAN");
                    break;
                case 7:
                    System.out.println("CREATE CLAN");
                    break;
                case 8:
                    System.out.println("ADD USER INTO CLAN");
                    break;
                case 0:
                    System.out.println("Closing ...");
                    break;
                default:
                    System.out.println("Invalid option ");
            }
        }while (option != 0);
    }

    public void close(){
    }

}
