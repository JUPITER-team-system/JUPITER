package com.management.jupiter.views;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.services.AssignmentService;
import com.management.jupiter.services.ClanService;

import java.util.List;
import java.util.Scanner;

/**
 * Vista del menú principal del Administrador.
 * Integra ClanService (US-02) y AssignmentService (US-04).
 */
public class AdminView {

    private final Scanner           scanner;
    private final ClanService       clanService;
    private final AssignmentService assignmentService;

    public AdminView(ClanService clanService, AssignmentService assignmentService) {
        this.scanner           = InputView.getScanner();
        this.clanService       = clanService;
        this.assignmentService = assignmentService;
    }

    public void menuAdmin() {
        int option;

        do {
            System.out.println(" ===== MENU ADMIN =====");
            System.out.println("1.  Ver Coders");
            System.out.println("2.  Ver TLs");
            System.out.println("3.  Ver Clanes");
            System.out.println("4.  Eliminar usuario");
            System.out.println("5.  Crear usuario");
            System.out.println("6.  Eliminar clan");
            System.out.println("7.  Crear clan");
            System.out.println("8.  Actualizar nombre de clan");
            System.out.println("9.  Asignar TL a clan");
            System.out.println("10. Asignar Coder a clan");
            System.out.println("11. Ver miembros de un clan");
            System.out.println("0.  Salir");
            System.out.print("Seleccione una opción: ");

            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.println("VIEW CODERS");
                    break;

                case 2:
                    System.out.println("VIEW TLS");
                    break;

                case 3:

                    break;

                case 4:
                    System.out.println("DELETE USER");
                    break;

                case 5:
                    System.out.println("CREATE USER");
                    break;

                case 6:

                    break;

                case 7:

                    break;

                case 8:

                    break;

                case 9:

                    break;

                case 10:

                    break;

                case 11:

                    break;

                case 0:
                    System.out.println("Cerrando...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (option != 0);
    }

