package com.management.jupiter;

import com.management.jupiter.controllers.UserController;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.views.AdminView;
import com.management.jupiter.views.CoderView;
import com.management.jupiter.views.TLView;

public class Main {
    public static void main(String[] args) throws Exception {
        User loggedUser = UserController.LoginController();

        if (loggedUser.getRole() == Role.CODER) {
            CoderView coderView = new CoderView();
            coderView.menuCoder();
            coderView.close();
        } else if (loggedUser.getRole() == Role.TL) {
            TLView tlView = new TLView();
            tlView.menuTL();
            tlView.close();
        } else if (loggedUser.getRole() == Role.ADMIN) {
            AdminView adminView = new AdminView();
            adminView.menuAdmin();
            adminView.close();
        }
    }
}
