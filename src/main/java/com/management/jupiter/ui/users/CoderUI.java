package com.management.jupiter.ui.users;

import com.management.jupiter.models.Coder;

public class CoderUI {

    public static void coder (Coder coder) {

        String coderUI = """
                -----------------------------
                |         Welcome:          |
                |            %s             |
                -----------------------------
                """.formatted(coder.getUsername())
                ;

        System.out.println(coderUI);

    }

    public static void coderDec(){

        String dec = """
                -----------------------------
                | 1) View All Coders.       |
                | 2) View Scrum Team.       |
                | 3) View All News.         |
                | 0) Exit                   |
                -----------------------------
                """
                ;

        System.out.println(dec);

    }
}
