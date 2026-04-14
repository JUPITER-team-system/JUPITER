package com.management.jupiter.ui.tl;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.services.AssignmentService;

import java.util.List;
import java.util.Scanner;

/**
 * US-09 – Vista de detalle de clan para el TL.
 *
 * Permite al TL:
 *   1. Ver la lista completa de coders de sus clanes
 *      (nombre, email, estado de asignación).
 *   2. Ver los detalles de cada "célula" (clan) al que pertenece.
 *   3. Seleccionar interactivamente una célula para ver su detalle.
 */
public class ClanDetailView {

    private final AssignmentService assignmentService;
    private final Scanner scanner;

    public ClanDetailView(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
        this.scanner = InputView.getScanner();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PUNTO DE ENTRADA: muestra todos los clanes del TL
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Muestra en consola todos los clanes del TL y, dentro de cada uno,
     * el listado de coders con nombre, email y estado.
     *
     * @param tl TL autenticado en sesión.
     */
    public void mostrarMisClanes(Tl tl) {
        List<Clan> misClanes = assignmentService.obtenerClanesDeTl(tl.getId());

        imprimirEncabezado("MIS CLANES – " + tl.getUsername() + " (" + tl.getTlType() + ")");

        if (misClanes.isEmpty()) {
            System.out.println("  No tienes clanes asignados actualmente.");
            imprimirLinea();
            return;
        }

        for (Clan clan : misClanes) {
            mostrarDetalleClan(clan);
        }

        imprimirLinea();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  SELECCIÓN INTERACTIVA DE CÉLULA (US-09 criterio 2)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Muestra el listado de clanes del TL y permite seleccionar uno
     * para ver su detalle completo.
     *
     * Criterio: "TL puede ver los detalles de cada célula de su clan".
     *
     * @param tl TL autenticado en sesión.
     */
    public void seleccionarYVerDetalleClan(Tl tl) {
        List<Clan> misClanes = assignmentService.obtenerClanesDeTl(tl.getId());

        imprimirEncabezado("SELECCIONAR CÉLULA – " + tl.getUsername());

        if (misClanes.isEmpty()) {
            System.out.println("  No tienes clanes (células) asignados.");
            imprimirLinea();
            return;
        }

        // Listar clanes disponibles con índice de selección
        for (int i = 0; i < misClanes.size(); i++) {
            Clan c = misClanes.get(i);
            System.out.printf("  [%d] %-20s  (%d coders, %d TLs)%n",
                    i + 1,
                    c.getName(),
                    c.getCoders().size(),
                    c.getTls().size());
        }
        System.out.println("  [0] Volver");
        imprimirLinea();

        int opcion = leerEntero("  Selecciona una célula: ");

        if (opcion == 0) return;

        if (opcion < 1 || opcion > misClanes.size()) {
            System.out.println("  [ERROR] Número fuera de rango.");
            return;
        }

        mostrarDetalleClan(misClanes.get(opcion - 1));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  DETALLE DE UN CLAN (célula)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Muestra el detalle completo de un clan:
     *   - Información general del clan.
     *   - Lista de coders con nombre, email y estado.
     *   - Lista de TLs asignados.
     *
     * @param clan Clan a mostrar.
     */
    public void mostrarDetalleClan(Clan clan) {
        imprimirLinea();
        System.out.println("  CLAN: " + clan.getName() + "  [ID: " + clan.getId() + "]");
        imprimirLinea();

        // ── TLs del clan ──────────────────────────────────────────────────
        List<Tl> tls = clan.getTls();
        System.out.println("  Team Leaders asignados (" + tls.size() + "):");
        if (tls.isEmpty()) {
            System.out.println("    (ninguno)");
        } else {
            for (Tl tl : tls) {
                System.out.printf("    · [%d] %-20s (%s)%n",
                        tl.getId(), tl.getUsername(), tl.getTlType());
            }
        }

        System.out.println();

        // ── Coders del clan ───────────────────────────────────────────────
        List<Coder> coders = clan.getCoders();
        System.out.println("  Coders asignados (" + coders.size() + "):");
        if (coders.isEmpty()) {
            System.out.println("    (ninguno)");
        } else {
            System.out.printf("    %-5s %-20s %-30s %-12s%n",
                    "ID", "Nombre", "Email", "Estado");
            System.out.println("    " + "-".repeat(70));
            for (Coder coder : coders) {
                String estado = resolverEstadoCoder(coder, clan);
                System.out.printf("    %-5d %-20s %-30s %-12s%n",
                        coder.getId(),
                        coder.getUsername(),
                        coder.getEmail(),
                        estado);
            }
        }
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  DETALLE DE UN CODER INDIVIDUAL
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Muestra los datos detallados de un coder específico dentro del clan.
     *
     * @param coder Coder a mostrar.
     * @param clan  Clan en contexto (para mostrar estado de asignación).
     */
    public void mostrarDetalleCoder(Coder coder, Clan clan) {
        imprimirEncabezado("DETALLE DE CODER");
        System.out.println("  ID       : " + coder.getId());
        System.out.println("  Nombre   : " + coder.getUsername());
        System.out.println("  Email    : " + coder.getEmail());
        System.out.println("  Rol      : " + coder.getRole());
        System.out.println("  Clan     : " + clan.getName());
        System.out.println("  Estado   : " + resolverEstadoCoder(coder, clan));
        imprimirLinea();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HELPERS PRIVADOS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Determina el estado de asignación de un coder dentro de un clan.
     * Regla simple: si está en la lista del clan → ACTIVO, de lo contrario → INACTIVO.
     */
    private String resolverEstadoCoder(Coder coder, Clan clan) {
        return clan.getCoders().contains(coder) ? "ACTIVO" : "INACTIVO";
    }

    /**
     * Lee un entero desde consola con manejo seguro de errores.
     * Si el input no es numérico, retorna -1.
     */
    private int leerEntero(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [ERROR] Ingresa un número válido.");
            return -1;
        }
    }

    private void imprimirLinea() {
        System.out.println("  " + "=".repeat(72));
    }

    private void imprimirEncabezado(String titulo) {
        imprimirLinea();
        System.out.println("  " + titulo);
        imprimirLinea();
    }
}
