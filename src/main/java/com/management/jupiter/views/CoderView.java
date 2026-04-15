package com.management.jupiter.views;
import java.util.Scanner;


public class CoderView {

    private final Scanner scanner;

    public CoderView(){
        scanner = InputView.getScanner();
    }

    public void menuCoder(){
        int option;

        do{
            System.out.println("===== MENU CODER =====");
            System.out.println("1. View News");
            System.out.println("2. View Team");
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
                case 0:
                    System.out.println("Closing ...");
                    break;
                default:
                    System.out.println("Invalid option ");
            }
        }while(option!=0);
    }

    public void close(){
    }

}
