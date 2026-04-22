package com.management.jupiter.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para hashing y verificación de contraseñas.
 *
 * Soporta dos escenarios:
 *  - Contraseñas ya hasheadas con BCrypt en la BD (hash comienza con $2a$ o $2b$).
 *  - Contraseñas en texto plano en la BD (usuarios creados antes del hashing).
 *
 * Al crear usuarios nuevos siempre se aplica BCrypt.
 */
public class PasswordHasher {

    /** Genera un hash BCrypt del texto plano. */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifica una contraseña contra el valor almacenado en BD.
     * Detecta automáticamente si el valor almacenado es:
     *   - Hash BCrypt  → usa BCrypt.checkpw()
     *   - Texto plano  → compara directamente (legacy)
     */
    public static boolean check(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        // Si el hash almacenado tiene formato BCrypt ($2a$, $2b$, $2y$)
        if (storedPassword.startsWith("$2a$") ||
            storedPassword.startsWith("$2b$") ||
            storedPassword.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plainPassword, storedPassword);
            } catch (Exception e) {
                // Hash malformado: caer al fallback
                return false;
            }
        }
        // Fallback: comparación directa (contraseñas legacy en texto plano)
        return plainPassword.equals(storedPassword);
    }
}
