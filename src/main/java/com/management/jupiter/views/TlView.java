package com.management.jupiter.views;
import com.management.jupiter.controllers.TlController;
import com.management.jupiter.models.Tl;
import com.management.jupiter.ui.users.TeamLeaderUI;
import com.management.jupiter.util.scanner.ScannerUtil;

public class TlView {

    private final ScannerUtil input;
    private final TlController controller;

    public TlView(ScannerUtil input, TlController controller){
        this.input = input;
        this.controller = controller;
    }

    public void show (Tl tl){

        int dec;

        do{

            TeamLeaderUI.teamLeader(tl);
            TeamLeaderUI.tlDec();
            dec = input.readInt("Which is your decision");

            switch (dec){
                case 1:
                    //Add Soon...
                    break;
                case 2:
                    //Add Soon...
                    break;
                case 3:
                    //Add Soon...
                    break;
                case 4:
                    coderManagement(tl);
                    break;
            }

        }while (dec != 0);

    }

    public void coderManagement(Tl tl){

        int dec;

        do{

            TeamLeaderUI.teamLeader(tl);
            TeamLeaderUI.tlCoder();
            dec = input.readInt("Which is your decision");

            switch (dec){
                case 1:
                    //Add Soon...
                    break;
                case 2:
                    //Add Soon...
                    break;
                case 3:
                    //Add Soon...
                    break;
                case 4:
                    //Add Soon...
                    break;
            }

        }while (dec != 0);

    }

}
