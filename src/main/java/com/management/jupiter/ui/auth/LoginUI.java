package com.management.jupiter.ui.auth;

public class LoginUI {

    public static void login(int attempts){

        String loginHeader = """
                -----------------------------
                |          LOGIN            |
                |   Left Attempts : %d      |
                -----------------------------
                """.formatted(attempts);

        System.out.println(loginHeader);

    }

}
