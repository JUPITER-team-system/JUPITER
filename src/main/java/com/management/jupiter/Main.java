package com.management.jupiter;

import com.management.jupiter.controllers.UserController;
import com.management.jupiter.views.AdminView;
import com.management.jupiter.views.CoderView;
import com.management.jupiter.views.LoginView;
import com.management.jupiter.views.TLView;

public class Main {
    public static void main(String[] args) throws Exception {
        UserController.LoginController();
/*        LoginView loginView = new LoginView();

        String loginSuccess = loginView.viewLogin();

        if(loginSuccess.equals("coder")){
            CoderView coderView = new CoderView();
            coderView.menuCoder();
            coderView.close();
        }else if(loginSuccess.equals("tl")){
            TLView tlView = new TLView();
            tlView.menuTL();
            tlView.close();
        }else if(loginSuccess.equals("admin")){
            AdminView adminView = new AdminView();
            adminView.menuAdmin();
            adminView.close();
        }

        loginView.closeScanner();*/

    }
}

