package com.management.jupiter.views;

import com.management.jupiter.controllers.AdminController;


import com.management.jupiter.controllers.CellController;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.AdminRepository;
import com.management.jupiter.repository.CellRepository;
import com.management.jupiter.repository.IaRepo;
import com.management.jupiter.services.AdminService;
import com.management.jupiter.services.AssignmentService;
import com.management.jupiter.services.CellServices;
import com.management.jupiter.services.ClanService;

import java.util.List;
import java.util.Scanner;

/**
 * Vista del menú principal del Administrador.
 * Integra ClanService (US-02) y AssignmentService (US-04).
 */
public class AdminView {

    private final Scanner scanner;
    private final ClanService clanService;
    private final AssignmentService assignmentService;

    public AdminView(ClanService clanService, AssignmentService assignmentService) {
        this.scanner = InputView.getScanner();
        this.clanService = clanService;
        this.assignmentService = assignmentService;
    }

    public void menuAdmin() {
        int option;

        do {
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
                case 12:
                    List<String> resp = IaRepo.useIA();
                    break;
                case 0:
                    System.out.println("Cerrando...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        } while (option != 0);
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
        Integer id = leerEntero("ID del clan a actualizar: ");
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
        Integer id = leerEntero("ID del clan a eliminar: ");
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
        Integer tlId = leerEntero("ID del TL: ");
        if (tlId == null) {
            return;
        }
        Integer clanId = leerIdDeClan("ID o nombre del clan: ");
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
        Integer coderId = leerEntero("ID del Coder: ");
        if (coderId == null) {
            return;
        }
        Integer clanId = leerIdDeClan("ID o nombre del clan: ");
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
        Integer clanId = leerIdDeClan("ID o nombre del clan: ");
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

    private Integer leerEntero(String mensaje) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Debes ingresar un ID numérico.");
            return null;
        }
    }

    private Integer leerIdDeClan(String mensaje) {
        System.out.print(mensaje);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("[ERROR] Debes ingresar un ID o nombre de clan.");
            return null;
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            try {
                com.management.jupiter.models.enums.Clan clanEnum =
                        com.management.jupiter.models.enums.Clan.valueOf(input.toUpperCase());
                return clanEnum.ordinal() + 1;
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] Clan inválido. Usa uno de estos valores: HAMILTON, THOMPSON, TESLA, NAKAMOTO.");
                return null;
            }
        }
    }

}
