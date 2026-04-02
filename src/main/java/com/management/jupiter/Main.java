package com.management.jupiter;

import com.management.jupiter.services.ClanService;
import com.management.jupiter.services.AssignmentService;

import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.TeamLeaderRepository;
import com.management.jupiter.repository.CoderRepository;

import com.management.jupiter.views.AdminView;
import com.management.jupiter.views.CoderView;
import com.management.jupiter.views.LoginView;
import com.management.jupiter.views.TLView;

public class Main {

    public static void main(String[] args) {

        // 🔹 Repositorios (acceso a datos)
        ClanRepository clanRepository = new ClanRepository();
        TeamLeaderRepository teamLeaderRepository = new TeamLeaderRepository();
        CoderRepository coderRepository = new CoderRepository();

        // 🔹 Servicios (lógica de negocio)
        ClanService clanService = new ClanService();
        AssignmentService assignmentService = new AssignmentService(
                clanRepository,
                teamLeaderRepository,
                coderRepository
        );

        // 🔹 Vista de login
        LoginView loginView = new LoginView();
        String loginSuccess = loginView.viewLogin();

        // 🔹 Flujo por roles
        if (loginSuccess.equals("coder")) {

            CoderView coderView = new CoderView();
            coderView.menuCoder();
            coderView.close();

        } else if (loginSuccess.equals("tl")) {

            TLView tlView = new TLView(assignmentService);
            tlView.menuTL();
            tlView.close();

        } else if (loginSuccess.equals("admin")) {

            AdminView adminView = new AdminView(clanService, assignmentService);
            adminView.menuAdmin();
            adminView.close();
        }

        loginView.closeScanner();
    }
}