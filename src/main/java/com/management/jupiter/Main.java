package com.management.jupiter;

import com.management.jupiter.controllers.*;
import com.management.jupiter.models.*;
import com.management.jupiter.repository.*;
import com.management.jupiter.repository.ai.AiProvider;
import com.management.jupiter.repository.ai.GeminiProvider;
import com.management.jupiter.repository.impl.*;
import com.management.jupiter.repository.interfaces.*;
import com.management.jupiter.security.LoginSession;
import com.management.jupiter.security.UserSession;
import com.management.jupiter.services.*;
import com.management.jupiter.util.scanner.ScannerUtil;
import com.management.jupiter.views.*;


import java.util.Scanner;

public class Main {

    public static void main (String[] args){

        //Interfaces:


        //Input:
        Scanner scanner = new Scanner(System.in);
        ScannerUtil input = new ScannerUtil(scanner);

        //Repositories:
        UserRepository adminRepo = new AdminRepositoryImpl();
        ClanRepository clanRepo = new ClanRepositoryImpl();
        CoderRepository coderRepo = new CoderRepository();
        TeamLeaderRepositoryImpl tlRepo = new TeamLeaderRepositoryImpl(clanRepo);

        AiProvider aiProvider = new GeminiProvider();
        CellRepositoryInterface cellRepository = new CellRepositoryInterfaceImpl();


        //Services:
        AssignmentService assignmentService = new AssignmentService(clanRepo, tlRepo, coderRepo);
        UserService userService = new UserService();
        AdminService adminService = new AdminService(userService, adminRepo);
        ClanService clanService = new ClanService(clanRepo);
        CellServices cellServices = new CellServices(aiProvider,cellRepository);

        //Controllers:
        UserController userController = new UserController();
        AdminController adminController = new AdminController(adminService);
        TlController tlController = new TlController();
        CoderController coderController = new CoderController();
        ClanController clanController = new ClanController(clanService);
        CellController cellController = new CellController(cellServices);

        //Views:
        LoginView login = new LoginView(input, userController);

        User user = login.login();

        LoginSession loggedUser = new UserSession(user);

        AdminView admin = new AdminView(input, loggedUser, adminController, clanController);
        TlView tl = new TlView(input, tlController, cellController);
        CoderView coder = new CoderView(input, coderController);

        if(user instanceof Admin loggedAdmin){
            admin.show(loggedAdmin);
        } else if (user instanceof Tl loggedTl) {
            tl.show(loggedTl);
        } else if (user instanceof Coder loggedCoder){
            coder.show(loggedCoder);
        }

    }
}