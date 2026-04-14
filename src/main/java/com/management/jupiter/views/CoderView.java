package com.management.jupiter.views;

import com.management.jupiter.controllers.AdminController;
import com.management.jupiter.models.Coder;
import com.management.jupiter.ui.users.CoderUI;
import com.management.jupiter.util.scanner.ScannerUtil;

public class CoderView {

    private final ScannerUtil input;
    private final AdminController controller;

    public CoderView (ScannerUtil input, AdminController controller) {
        this.input = input;
        this.controller = controller;
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
                    //Add Soon...
                    break;
            }

        } while (dec != 0);

    }

}