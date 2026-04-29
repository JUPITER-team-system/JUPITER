package com.management.jupiter.models.enums;

/**
 * Tipo de Team Leader / Especialidad de usuario.
 * Solo se permiten dos valores válidos:
 *   - PROGRAMACION
 *   - INGLES
 */
public enum TlType {
    PROGRAMACION,
    INGLES;

    /**
     * Valida que el valor dado corresponda a una especialidad válida.
     * @throws IllegalArgumentException si el valor no es válido.
     */
    public static TlType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La especialidad no puede estar vacía.");
        }
        try {
            return TlType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Especialidad inválida: '" + value + "'. Solo se permiten: PROGRAMACION, INGLES");
        }
    }
}
