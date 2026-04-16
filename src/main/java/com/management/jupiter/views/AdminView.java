package com.management.jupiter.views;
import com.management.jupiter.controllers.AdminController;


import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.AdminRepository;
import com.management.jupiter.services.AdminService;
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

    public void menuAdmin(){
        int option;

        do{
            System.out.println(" ===== MENU ADMIN =====");
            System.out.println("1. View Coders");
            System.out.println("2. View Tls");
            System.out.println("3. View Clanes");
            System.out.println("4. Delete user");
            System.out.println("5. Create user");
            System.out.println("6. Delete clan");
            System.out.println("7. Create clan");
            System.out.println("8. Add user into clan");
            System.out.println("9.  Assign TL to a clan");
            System.out.println("10. Assign Coder to a clan");
            System.out.println("11. View clan members");
            System.out.println("0.  Exit");
            System.out.println("Select a option");

            String optionInput = scanner.nextLine();
            try {
                option = Integer.parseInt(optionInput);
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1:
                    System.out.println("VIEW CODERS");
                    AdminService.getUsersByRol("CODER");
                    break;

                case 2:
                    System.out.println("VIEW TLS");
                    AdminService.getUsersByRol("TL");
                    break;

                case 3:
                    // US-02 – Listar clanes
                    listarClanes();
                    break;

                case 4:
                    System.out.println("DELETE USER");
                    AdminController.deleteUser();
                    break;

                case 5:
                    System.out.println("CREATE USER");
                    AdminController.createUser();
                    break;

                case 6:
                    // US-02 – Eliminar clan
                    eliminarClan();
                    break;

                case 7:
                    // US-02 – Crear clan
                    crearClan();
                    break;

                case 8:
                    // US-02 – Actualizar clan
                    actualizarClan();
                    break;

                case 9:
                    // US-04 – Asignar TL a clan
                    asignarTlAClan();
                    break;

                case 10:
                    // US-04 – Asignar Coder a clan
                    asignarCoderAClan();
                    break;

                case 11:
                    // US-04 – Ver miembros de un clan
                    verMiembrosDeClan();
                    break;
                case 0:
                    System.out.println("Cerrando...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }while (option != 0);
    }

    // ── US-02: CRUD de Clanes ────────────────────────────────────────────────

    private void crearClan() {
        System.out.print("Nombre del nuevo clan: ");
        String nombre = scanner.nextLine();
        try {
            Clan nuevo = clanService.crearClan(nombre);
            System.out.println("Clan creado exitosamente: " + nuevo);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void listarClanes() {
        List<Clan> clanes = clanService.listarClanes();
        if (clanes.isEmpty()) {
            System.out.println("No hay clanes registrados.");
            return;
        }
        System.out.println("=== CLANES REGISTRADOS ===");
        for (Clan c : clanes) {
            System.out.println("  " + c);
        }
    }

    private void actualizarClan() {
        String id = leerIdDeClan("ID del clan a actualizar: ");
        if (id == null) {
            return;
        }
        System.out.print("Nuevo nombre: ");
        String nuevoNombre = scanner.nextLine();
        try {
            clanService.actualizarClan(id, nuevoNombre);
            System.out.println("Clan actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void eliminarClan() {
        String id = leerIdDeClan("ID del clan a eliminar: ");
        if (id == null) {
            return;
        }
        try {
            clanService.eliminarClan(id);
            System.out.println("Clan eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // ── US-04: Asignaciones ──────────────────────────────────────────────────

    private void asignarTlAClan() {
        String tlId = leerIdDeClan("ID del TL: ");
        if (tlId == null) {
            return;
        }
        String clanId = leerIdDeClan("ID o nombre del clan: ");
        if (clanId == null) {
            return;
        }
        try {
            assignmentService.asignarTlAClan(tlId, clanId);
            System.out.println("TL asignado al clan correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void asignarCoderAClan() {
        String coderId = leerIdDeClan("ID del Coder: ");
        if (coderId == null) {
            return;
        }
        String clanId = leerIdDeClan("ID o nombre del clan: ");
        if (clanId == null) {
            return;
        }
        try {
            assignmentService.asignarCoderAClan(coderId, clanId);
            System.out.println("Coder asignado al clan correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void verMiembrosDeClan() {
        String clanId = leerIdDeClan("ID o nombre del clan: ");
        if (clanId == null) {
            return;
        }
        try {
            List<Tl> tls = assignmentService.obtenerTlsDeClan(clanId);
            List<Coder> coders = assignmentService.obtenerCodersDeClan(clanId);

            System.out.println("=== TLs del clan ===");
            if (tls.isEmpty()) {
                System.out.println("  (ninguno)");
            } else {
                tls.forEach(t -> System.out.println("  " + t));
            }

            System.out.println("=== Coders del clan ===");
            if (coders.isEmpty()) {
                System.out.println("  (ninguno)");
            } else {
                coders.forEach(c -> System.out.println("  " + c));
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void close() {
    }

    private Long leerEntero(String mensaje) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim();
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Debes ingresar un ID numérico.");
            return null;
        }
    }

    private String leerIdDeClan(String mensaje) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("[ERROR] Debes ingresar un ID o nombre de clan.");
            return null;
        }

        // Try to parse as UUID first, if not, try as enum name
        if (input.contains("-")) {
            return input; // Assume it's a UUID
        } else {
            try {
                com.management.jupiter.models.enums.Clan clanEnum =
                        com.management.jupiter.models.enums.Clan.valueOf(input.toUpperCase());
                return clanEnum.name(); // Return the enum name as ID
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] Clan inválido. Usa uno de estos valores: HAMILTON, THOMPSON, TESLA, NAKAMOTO.");
                return null;
            }
        }
    }

}
