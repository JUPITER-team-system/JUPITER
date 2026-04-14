package com.management.jupiter.ui.users;

import com.management.jupiter.models.Tl;

public class TeamLeaderUI {

    public static void teamLeader (Tl tl){

        String tlUI = """
                -----------------------------
                |    Welcome To TLs Menu    |
                |            %s             |
                -----------------------------
                """.formatted(tl.getUsername())
                ;

        System.out.println(tlUI);

    }

    public static void tlDec(){

        String dec = """
                -----------------------------
                | 1) Add News.              |
                | 2) View News.             |
                | 3) View Team.             |
                | 4) Coder management.      |
                | 0) Exit                   |
                -----------------------------
                """
                ;

        System.out.println(dec);

    }

    public static void tlCoder(){

        String coder = """
                -----------------------------
                | 1) Add Coder.             |
                | 2) Delete coder           |
                | 3) Add Scrum Team.        |
                | 4) Delete Scrum Team      |
                | 0) Exit                   |
                -----------------------------
                """
                ;

        System.out.println(coder);

    }

}
