package com.management.jupiter;

import com.management.jupiter.services.ClanService;
import com.management.jupiter.services.AssignmentService;

import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.TeamLeaderRepository;
import com.management.jupiter.repository.CoderRepository;

import com.management.jupiter.controllers.UserController;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.views.AdminView;
import com.management.jupiter.views.CoderView;
import com.management.jupiter.views.TLView;
import com.management.jupiter.models.enums.Role;

public class Main {

    public static void main(String[] args) throws Exception {
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

        User loggedUser = UserController.login();
        if (loggedUser == null) {
            return;
        }

        // 🔹 Flujo por roles
        if (loggedUser.getRole() == Role.CODER) {
            CoderView coderView = new CoderView();
            coderView.menuCoder();
            coderView.close();
        } else if (loggedUser.getRole() == Role.TL) {
            TLView tlView = new TLView(assignmentService);
            if (loggedUser instanceof Tl tl) {
                tlView.setTlSesion(tl);
            }
            tlView.menuTL();
            tlView.close();
        } else if (loggedUser.getRole() == Role.ADMIN) {
            AdminView adminView = new AdminView(clanService, assignmentService);
            adminView.menuAdmin();
            adminView.close();
        }
    }
}
