package com.management.jupiter.views;

import com.management.jupiter.controllers.AdminController;
import com.management.jupiter.controllers.ClanController;
import com.management.jupiter.models.*;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.enums.*;
import com.management.jupiter.security.LoginSession;
import com.management.jupiter.ui.users.AdminUI;
import com.management.jupiter.ui.users.ClanDetailUI;
import com.management.jupiter.util.scanner.ScannerUtil;

import java.util.Optional;

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
                    ClanDetailUI.coderList(adminController.getAll());
                    break;
                case 2:
                    //Add Soon...
                    break;
                case 3:
                    ClanDetailUI.clanList(clanController.readAll());
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
                    createClan();
                    break;
                case 2:
                    deleteClan();
                    break;
                case 3:
                    updateClan();
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

    private void createClan() {

        String name = input.readString("Clan name: ");
        String desc = input.readString("Clan description: ");

        clanController.createClan(name, desc);

    }

    private void deleteClan () {

        ClanDetailUI.clanList(clanController.readAll());

        String value = input.readString("Enter an id or name to delete clan: ");

        clanController.deleteClan(value);

    }

    private void updateClan () {

        ClanDetailUI.clanList(clanController.readAll());

        String value = input.readString("Enter an id or name to edit clan: ");
        Optional<Clan> clanOp = clanController.readIdOrName(value);

        if (clanOp.isEmpty()){

            System.out.println("Don't found Clan. Try again!");
            return;

        }

        Clan clan = clanOp.get();

        ClanDetailUI.clanUpdater(clan);

        int op = input.readInt("what's your decision?: ");

        if (op == 1) {

            clan.setName(input.readString("What's the new name?: "));
            clan.setDescription(input.readString("What's the new description?: "));

        } else if (op == 2) {

            managementClanMembers(clan);

        }

        clanController.updateClan(clan);

    }

    public void managementClanMembers(Clan clan){

        ClanDetailUI.clanMemberUpdate(clan);

        int op;

        do {

            op = input.readInt(">");

            switch (op) {
                case 1 -> {

                    String id = input.readString("Id of coder to add: ");
                    Optional<User> user = adminController.findById(id);

                    user.ifPresent(u -> {

                        if (u instanceof Coder coderFound){

                            clan.getCoders().add(coderFound);
                            System.out.println("The Coder:" + coderFound.getUsername() + "is found");

                        }else {

                            System.out.println("User found, but it's not a Coder");

                        }

                    });

                }
                case 2 -> {

                    String id = input.readString("Id of coder to remove: ");
                    clan.getCoders().removeIf(c -> c.getId().equals(id));

                }
                case 3 -> {

                    String id = input.readString("Id of TL to add: ");
                    Optional<User> user = adminController.findById(id);

                    user.ifPresent(u ->{

                        if (u instanceof Tl tlFound){

                            clan.getTls().add(tlFound);
                            System.out.println("The TL:" + tlFound.getUsername() + "is found");

                        } else {

                            System.out.println("User found, but it's not a TL");

                        }

                    });

                }
                case 4 -> {

                    String id = input.readString("Id of TL to remove: ");
                    clan.getTls().removeIf(t -> t.getId().equals(id));

                }
            }

        } while (op != 0);

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