package com.management.jupiter.fx;

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
import com.management.jupiter.fx.controllers.LoginController;
import com.management.jupiter.fx.controllers.AdminDashboardController;
import com.management.jupiter.fx.controllers.TlDashboardController;
import com.management.jupiter.fx.controllers.CoderDashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class JupiterFXApplication extends Application {
    
    private static Stage primaryStage;
    private static UserController userController;
    private static AdminController adminController;
    private static TlController tlController;
    private static CoderController coderController;
    private static ClanController clanController;
    private static CellController cellController;
    private static InformationController informationController;
    private static LoginSession loginSession;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        initializeServices();
        showLoginScreen();
        primaryStage.show();
    }
    
    private void initializeServices() {
        // Repositories
        UserRepository adminRepo = new AdminRepositoryImpl();
        ClanRepository clanRepo = new ClanRepositoryImpl();
        CoderRepository coderRepo = new CoderRepository();
        TeamLeaderRepositoryImpl tlRepo = new TeamLeaderRepositoryImpl(clanRepo);
        
        AiProvider aiProvider = new GeminiProvider();
        CellRepositoryInterface cellRepository = new CellRepositoryInterfaceImpl();
        InformationRepository informationRepository = new InformationRepositoryImpl();
        
        // Services
        AssignmentService assignmentService = new AssignmentService(clanRepo, tlRepo, coderRepo);
        UserService userService = new UserService();
        AdminService adminService = new AdminService(userService, adminRepo);
        ClanService clanService = new ClanService(clanRepo);
        CellServices cellServices = new CellServices(aiProvider, cellRepository);
        InformationService informationService = new InformationService(informationRepository);
        
        // Controllers
        userController = new UserController();
        adminController = new AdminController(adminService);
        tlController = new TlController();
        coderController = new CoderController();
        clanController = new ClanController(clanService);
        cellController = new CellController(cellServices);
        informationController = new InformationController(informationService);
    }
    
    public static void showLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(JupiterFXApplication.class.getResource("/views/login.fxml")));
        Parent root = loader.load();
        
        LoginController controller = loader.getController();
        controller.setControllers(userController, adminController, tlController, coderController, 
                               clanController, cellController, informationController);
        
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(Objects.requireNonNull(JupiterFXApplication.class.getResource("/styles/main.css")).toExternalForm());
        
        primaryStage.setTitle("Jupiter Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }
    
    public static void showAdminDashboard(User user) throws IOException {
        loginSession = new UserSession(user);
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(JupiterFXApplication.class.getResource("/views/admin-dashboard.fxml")));
        Parent root = loader.load();
        
        AdminDashboardController controller = loader.getController();
        controller.setUserData((Admin) user, loginSession, adminController, clanController);
        controller.initializeDashboard();
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(JupiterFXApplication.class.getResource("/styles/main.css")).toExternalForm());
        
        primaryStage.setTitle("Jupiter Management System - Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }
    
    public static void showTlDashboard(User user) throws IOException {
        // Validate user is a TL
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getRole() != com.management.jupiter.models.enums.Role.TL) {
            throw new IllegalArgumentException("User is not a Team Leader. Role: " + user.getRole());
        }
        
        if (!(user instanceof Tl)) {
            throw new IllegalArgumentException("User is not an instance of TL");
        }
        
        loginSession = new UserSession(user);
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(JupiterFXApplication.class.getResource("/views/tl-dashboard.fxml")));
        Parent root = loader.load();
        
        TlDashboardController controller = loader.getController();
        controller.setUserData((Tl) user, loginSession, tlController, cellController, informationController, clanController);
        controller.initializeDashboard();
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(JupiterFXApplication.class.getResource("/styles/main.css")).toExternalForm());
        
        primaryStage.setTitle("Jupiter Management System - Team Leader Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }
    
    public static void showCoderDashboard(User user) throws IOException {
        loginSession = new UserSession(user);
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(JupiterFXApplication.class.getResource("/views/coder-dashboard.fxml")));
        Parent root = loader.load();
        
        CoderDashboardController controller = loader.getController();
        controller.setUserData((Coder) user, loginSession, coderController, informationController, cellController);
        controller.initializeDashboard();
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(JupiterFXApplication.class.getResource("/styles/main.css")).toExternalForm());
        
        primaryStage.setTitle("Jupiter Management System - Coder Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }
    
    public static void logout() throws IOException {
        loginSession = null;
        showLoginScreen();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
