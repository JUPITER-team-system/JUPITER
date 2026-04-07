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
                    // US-02 – Listar clanes
                    listarClanes();
                    break;

                case 4:
                    System.out.println("DELETE USER");
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
        System.out.print("ID del clan a actualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
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
        System.out.print("ID del clan a eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try {
            clanService.eliminarClan(id);
            System.out.println("Clan eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // ── US-04: Asignaciones ──────────────────────────────────────────────────

    private void asignarTlAClan() {
        System.out.print("ID del TL: ");
        int tlId = scanner.nextInt();
        System.out.print("ID del clan: ");
        int clanId = scanner.nextInt();
        scanner.nextLine();
        try {
            assignmentService.asignarTlAClan(tlId, clanId);
            System.out.println("TL asignado al clan correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void asignarCoderAClan() {
        System.out.print("ID del Coder: ");
        int coderId = scanner.nextInt();
        System.out.print("ID del clan: ");
        int clanId = scanner.nextInt();
        scanner.nextLine();
        try {
            assignmentService.asignarCoderAClan(coderId, clanId);
            System.out.println("Coder asignado al clan correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void verMiembrosDeClan() {
        System.out.print("ID del clan: ");
        int clanId = scanner.nextInt();
        scanner.nextLine();
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
}
