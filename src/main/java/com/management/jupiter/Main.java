package com.management.jupiter;

import com.management.jupiter.services.ClanService;
import com.management.jupiter.services.AssignmentService;
import com.management.jupiter.services.UserService;

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
import com.management.jupiter.persistance.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Start JUPITER project...");

        // Establecer conexión a la base de datos
        try(Connection conn = DatabaseConnection.getConnection()){
            if (conn != null && !conn.isClosed()){
                System.out.println("Database connection successful. RUNNING.");
                System.out.println("Connected to database: " + conn.getCatalog());
            }
        }catch (Exception e){
            System.out.println("[ERROR]: Cannot connect to database");
            e.printStackTrace();
        }

        // Crear usuarios de prueba si no existen
        UserService.createTestUsers();

        // Repositorios (necesarios para AssignmentService)
        ClanRepository clanRepository = new ClanRepository();
        TeamLeaderRepository teamLeaderRepository = new TeamLeaderRepository();
        CoderRepository coderRepository = new CoderRepository();

        // Servicios (lógica de negocio)
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

        // Flujo por roles
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
