package com.management.jupiter.security;

import com.management.jupiter.models.User;

/**
 * Singleton que mantiene la sesión del usuario actualmente autenticado
 * en la interfaz gráfica. Permite que los controladores FX accedan al
 * usuario logueado sin acoplarse entre sí.
 */
public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /** Registra al usuario que acaba de autenticarse. */
    public void login(User user) {
        this.currentUser = user;
    }

    /** Elimina la sesión activa (logout). */
    public void logout() {
        this.currentUser = null;
    }

    /** Devuelve el usuario actualmente logueado, o null si no hay sesión. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Indica si existe una sesión activa. */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
