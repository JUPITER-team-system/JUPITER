package com.management.jupiter.views;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Tl;
import com.management.jupiter.services.AssignmentService;
import com.management.jupiter.ui.tl.ClanDetailView;

import java.util.List;
import java.util.Scanner;

/**
 * Vista del menú principal del TL.
 * Integra ClanDetailView para la US-09.
 */
public class TLView {

    private final Scanner        scanner;
    private final ClanDetailView clanDetailView;
    private final AssignmentService assignmentService;
    private Tl                   tlSesion; // TL autenticado en sesión

    public TLView(AssignmentService assignmentService) {
        this.scanner            = InputView.getScanner();
        this.assignmentService  = assignmentService;
        this.clanDetailView     = new ClanDetailView(assignmentService);
    }

    /** Establece el TL de sesión después del login. */
    public void setTlSesion(Tl tl) {
        this.tlSesion = tl;
    }

    public void menuTL() {
        int option;

        do {
            System.out.println(" ===== MENU TL =====");
            System.out.println("1. Ver noticias");
            System.out.println("2. Ver mis clanes y coders");
            System.out.println("3. Ver detalle de un clan");
            System.out.println("4. Agregar coder");
            System.out.println("5. Eliminar coder");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            option = leerEntero();

            switch (option) {
                case 1:
                    System.out.println("NEWS");
                    break;

                case 2:
                    // US-09: ver todos los clanes del TL con su lista de coders
                    if (tlSesion != null) {
                        clanDetailView.mostrarMisClanes(tlSesion);
                    } else {
                        System.out.println("[ERROR] No hay TL en sesión.");
                    }
                    break;

                case 3:
                    // US-09: seleccionar y ver detalle de una célula específica
                    if (tlSesion != null) {
                        clanDetailView.seleccionarYVerDetalleClan(tlSesion);
                    } else {
                        System.out.println("[ERROR] No hay TL en sesión.");
                    }
                    break;

                case 4:
                    System.out.println("ADD CODER");
                    break;

                case 5:
                    System.out.println("DELETE CODER");
                    break;

                case 0:
                    System.out.println("Cerrando...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        } while (option != 0);
    }

    /**
     * Lee un entero desde consola con manejo seguro de errores.
     * Si el input no es numérico retorna -1 para que el switch caiga en default.
     */
    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Ingresa un número válido.");
            return -1;
        }
    }

    public void close() {
    }
}
