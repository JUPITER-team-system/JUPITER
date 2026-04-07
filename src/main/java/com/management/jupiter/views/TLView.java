package com.management.jupiter.views;
import java.util.Scanner;

public class TLView {

    private final Scanner scanner;

    public TLView(){
        scanner = InputView.getScanner();
    }

    public void menuTL(){
        int option;

        do{
            System.out.println(" ===== MENU TL =====");
            System.out.println("1. View News");
            System.out.println("2. View Team");
            System.out.println("3. Add Coder");
            System.out.println("4. Delete Coder");
            System.out.println("5. Create New");
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
                    System.out.println("NEWS");
                    break;
                case 2:
                    System.out.println("TEAM");
                    break;
                case 3:
                    System.out.println("ADD CODER");
                    break;
                case 4:
                    System.out.println("DELETE CODER");
                    break;
                case 5:
                    System.out.println("CREATE NEW");
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
