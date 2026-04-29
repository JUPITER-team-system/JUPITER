package com.management.jupiter.views;

import com.management.jupiter.controllers.CoderController;
import com.management.jupiter.controllers.InformationController;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Information;
import com.management.jupiter.ui.users.CoderUI;
import com.management.jupiter.util.scanner.ScannerUtil;

import java.util.List;

public class CoderView {

    private final ScannerUtil input;
    private final CoderController controller;
    private final InformationController informationController;

    public CoderView (ScannerUtil input, CoderController controller, InformationController informationController) {
        this.input = input;
        this.controller = controller;
        this.informationController = informationController;
    }

    public void show (Coder coder) {

        CoderUI.coder(coder);
        CoderUI.coderDec();

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
                    viewInformation(coder);
                    break;
            }

        } while (dec != 0);

    }

    private void viewInformation(Coder coder) {
        List<Information> informationList = informationController.findByUserClan(coder);
        if (informationList.isEmpty()) {
            System.out.println("There is no information to show.");
            return;
        }

        System.out.println("\nClan information");
        for (int i = 0; i < informationList.size(); i++) {
            Information information = informationList.get(i);
            System.out.printf("%n%d) %s%n", i + 1, information.getTitle());
            System.out.println(information.getMessage());
        }
        System.out.println();
    }

}
