package com.management.jupiter.views;

import com.management.jupiter.controllers.CellController;
import com.management.jupiter.controllers.InformationController;
import com.management.jupiter.controllers.TlController;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Information;
import com.management.jupiter.models.Tl;
import com.management.jupiter.ui.users.TeamLeaderUI;
import com.management.jupiter.util.scanner.ScannerUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TlView {

    private final ScannerUtil input;
    private final TlController controller;
    private final CellController cellController;
    private final InformationController informationController;

    public TlView(ScannerUtil input, TlController controller, CellController cellController, InformationController informationController) {
        this.input = input;
        this.controller = controller;
        this.cellController = cellController;
        this.informationController = informationController;
    }

    public void show(Tl tl) {

        int dec;

        do {

            TeamLeaderUI.teamLeader(tl);
            TeamLeaderUI.tlDec();
            dec = input.readInt("Which is your decision");

            switch (dec) {
                case 1:
                    createInformation(tl);
                    break;
                case 2:
                    viewInformation(tl);
                    break;
                case 3:
                    viewTeam(tl);
                    break;
                case 4:
                    coderManagement(tl);
                    break;
            }

        } while (dec != 0);

    }

    public void coderManagement(Tl tl) {

        int dec;

        do {

            TeamLeaderUI.teamLeader(tl);
            TeamLeaderUI.tlCoder();
            dec = input.readInt("Which is your decision");

            switch (dec) {
                case 1:
                    //Add Soon...
                    break;
                case 2:
                    //Add Soon...
                    break;
                case 3:
                    createCellsForManagedClan(tl);
                    break;
                case 4:
                    //Add Soon...
                    break;
            }

        } while (dec != 0);

    }

    private void createCellsForManagedClan(Tl tl) {
        Optional<Clan> clan = selectManagedClan(tl);

        if (clan.isEmpty()) {
            return;
        }

        int cellsQuantity = input.readInt("How many cells do you want to create?");
        String theme = input.readString("Which theme should be used for the cells?");
        cellController.createCell(cellsQuantity, theme, clan.get());
    }

    private void viewTeam(Tl tl) {
        Optional<Clan> clan = selectManagedClan(tl);

        if (clan.isEmpty()) {
            return;
        }

        Map<String, List<Coder>> codersByCell = cellController.getCodersGroupedByCell(clan.get());
        if (codersByCell.isEmpty()) {
            System.out.println("This clan does not have assigned coders.");
            return;
        }

        System.out.printf("%nTeam for clan: %s%n", clan.get().getName());
        codersByCell.forEach((cellName, coders) -> {
            System.out.printf("%nCell: %s (%d coder%s)%n", cellName, coders.size(), coders.size() == 1 ? "" : "s");
            for (int i = 0; i < coders.size(); i++) {
                Coder coder = coders.get(i);
                System.out.printf("%d) %s%n", i + 1, coder.getUsername());
            }
        });
        System.out.println();
    }

    private void createInformation(Tl tl) {
        Optional<Clan> clan = selectManagedClan(tl);

        if (clan.isEmpty()) {
            return;
        }

        String title = input.readString("Information title (max 40 characters):");
        String message = input.readString("Information message:");

        try {
            informationController.createInformation(title, message, clan.get());
            System.out.println("Information created.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewInformation(Tl tl) {
        Optional<Clan> clan = selectManagedClan(tl);

        if (clan.isEmpty()) {
            return;
        }

        printInformation(informationController.findByClan(clan.get()), "Information for clan: " + clan.get().getName());
    }

    private void printInformation(List<Information> informationList, String header) {
        if (informationList.isEmpty()) {
            System.out.println("There is no information to show.");
            return;
        }

        System.out.printf("%n%s%n", header);
        for (int i = 0; i < informationList.size(); i++) {
            Information information = informationList.get(i);
            System.out.printf("%n%d) %s%n", i + 1, information.getTitle());
            System.out.println(information.getMessage());
        }
        System.out.println();
    }

    private Optional<Clan> selectManagedClan(Tl tl) {
        if (tl.getClans().isEmpty()) {
            System.out.println("You do not have assigned clans to manage.");
            return Optional.empty();
        }

        System.out.println("Select the clan to manage:");
        for (int i = 0; i < tl.getClans().size(); i++) {
            Clan clan = tl.getClans().get(i);
            System.out.printf("%d) %s%n", i + 1, clan.getName());
        }

        int selectedOption = input.readInt("Choose a clan number");
        if (selectedOption < 1 || selectedOption > tl.getClans().size()) {
            System.out.println("Invalid clan option.");
            return Optional.empty();
        }

        return Optional.of(tl.getClans().get(selectedOption - 1));
    }

}
