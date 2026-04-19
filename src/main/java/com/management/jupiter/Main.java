package com.management.jupiter;

import com.management.jupiter.controllers.*;
import com.management.jupiter.models.*;
import com.management.jupiter.repository.*;
import com.management.jupiter.services.*;
import com.management.jupiter.util.scanner.ScannerUtil;
import com.management.jupiter.views.*;


import java.util.Scanner;

public class Main {

    public static void main (String[] args){

        //Input
        Scanner scanner = new Scanner(System.in);
        ScannerUtil input = new ScannerUtil(scanner);

        //Repositories:
        AdminRepository adminRepo = new AdminRepository();
        ClanRepository clanRepo = new ClanRepository();
        CoderRepository coderRepo = new CoderRepository();
        TeamLeaderRepository tlRepo = new TeamLeaderRepository(clanRepo);
        UserRepository userRepo = new UserRepository();

        //Services:
        AssignmentService assignmentService = new AssignmentService(clanRepo, tlRepo, coderRepo);
        AdminService adminService = new AdminService(adminRepo);


        //Controllers:
        UserController userController = new UserController();
        AdminController adminController = new AdminController(adminService);
        TlController tlController = new TlController();
        CoderController coderController = new CoderController();

        //Views:
        LoginView login = new LoginView(input, userController);
        AdminView admin = new AdminView(input, adminController);
        TlView tl = new TlView(input, tlController);
        CoderView coder = new CoderView(input, coderController);

        User user = login.login();

        if(user instanceof Admin loggedAdmin){
            admin.show(loggedAdmin);
        } else if (user instanceof Tl loggedTl) {
            tl.show(loggedTl);
        } else if (user instanceof Coder loggedCoder){
            coder.show(loggedCoder);
        }

    }
}