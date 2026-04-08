package com.management.jupiter.models.enums;

/**
 * Tipo de Team Leader.
 * Usado por AssignmentService para validar los límites por clan:
 *   - PROGRAMACION : máximo 1 por clan
 *   - INGLES       : máximo 2 por clan
 */
public enum TlType {
    PROGRAMACION,
    INGLES
}
