package com.management.jupiter.ui.users;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.TlType;

import java.util.List;

public class ClanDetailUI {

    public static void showCoderDetails(Coder coder, String status, String clan ) {

        String border = "-".repeat(24);
        String coders = """
                |%s
                | Coder Details        |
                |%s
                | ID : %s
                | Name: %s
                | Email: %s
                | Rol: %s
                | Clan: %s
                | Status: %s
                %s
                """.formatted(border, border, coder.getId(), coder.getUsername(),
                coder.getEmail(), coder.getRole(), clan, status, border);

        System.out.println(coders);
        printLine();
    }

    public static void coderList (List<User> user) {

        printHeaders("Coders List");

        for (int i = 0 ; i < user.size(); i ++){

            User u = user.get(1);

            if (u instanceof Coder c){

                System.out.printf("[%d] ID:%-20s | Name: id:%-20s | Email: %s",
                        (i + 1), c.getId(), c.getUsername(), c.getEmail()
                );

            }

        }

        printLine();

    }


    public static void clanList (List<Clan> clans){

        printHeaders("Clan List");

        for (int i = 0; i < clans.size(); i ++){

            Clan c = clans.get(i);

            System.out.printf("  [%d] %-20s | Coders: %d%n | TL: %s%n" ,
                    (i + 1), c.getName(),
                    c.getCoders().size(),
                    c.getTls()
                            .stream()
                            .filter(tl -> tl.getTlType() == TlType.PROGRAMACION)
                            .map(User::getUsername)
                            .findFirst()
                            .orElse("This clan don't have TL")
            );
        }

        printLine();

    }

    public static void clanUpdater(Clan clan) {

        String update = """
                -------------------------------------
                |       Updater of clans, Clan:
                |                 %s
                -------------------------------------
                | 1) Edit clan information (name/desc)
                | 2) manage member (Coder/Tls)
                -------------------------------------
                """.formatted(clan.getName());

        System.out.println(update);

    }

    public static void clanMemberUpdate (Clan clan) {

        String update = """
                -------------------------------------
                |     Updater of Members, Clan:
                |                 %s
                -------------------------------------
                | 1) Add coder
                | 2) Get out Coder
                | 3) Add Tl
                | 4) Get out TL
                | 0) Back
                -------------------------------------
                """.formatted(clan.getName());

        System.out.println(update);

    }

    public static void printLine() {
        System.out.println("  " + "=".repeat(72));
    }

    public static void printHeaders(String title) {
        printLine();
        System.out.println("  " + title);
        printLine();
    }

}
