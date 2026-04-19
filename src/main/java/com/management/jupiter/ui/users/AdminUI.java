package com.management.jupiter.ui.users;

import com.management.jupiter.models.Admin;

public class AdminUI {

    public static void admin(Admin admin){

        String adminUI = """
                -----------------------------
                |   Welcome To Admin Menu   |
                |            %s             |
                -----------------------------
                """.formatted(admin.getUsername())
                ;

        System.out.println(adminUI);

    }

    public static void adminDec(){

        String dec = """
                -----------------------------
                | 1) View all coders.       |
                | 2) View all TLs.          |
                | 3) View all Clans.        |
                | 4) Clan management.       |
                | 5) User management.       |
                | 0) Exit                   |
                -----------------------------
                """
                ;

        System.out.println(dec);

    }

    public static void clanManage() {

        String clan = """
                -----------------------------
                | 1) Create Clan.           |
                | 2) Delete Clan.           |
                | 3) Update Name.           |
                | 4) Assign TL To Clan.     |
                | 5) Assign Coder To Clan.  |
                | 6) View Clan Members.     |
                | 0) Back.                  |
                -----------------------------
                """
                ;

        System.out.println(clan);

    }

    public static void userManage () {

        String user = """
                -----------------------------
                | 1) Create User.           |
                | 2) Delete User.           |
                | 3) Edit User.             |
                | 0) Exit                   |
                -----------------------------
                """
                ;

        System.out.println(user);

    }

    public static void userOptions () {

        String userOptions = """
                -----------------------------
                | 1) Name.                  |
                | 2) Email.                 |
                | 3) Password.              |
                | 4) Role                   |
                | 0) Exit                   |
                -----------------------------
                """
                ;

        System.out.println(userOptions);

    }
}
