package com.management.jupiter.models;

public class Attempts {
    // 📦 Clase interna para manejar intentos
    public int failedAttempts = 0;
    public long blockedUntil = 0;

    public void increase() {
        failedAttempts++;
    }

    public void block(int seconds) {
        blockedUntil = System.currentTimeMillis() + (seconds * 1000L);
        failedAttempts = 0;
    }

    public void reset() {
        failedAttempts = 0;
        blockedUntil = 0;
    }
    @Override
    public String toString(){
        return String.format("""
                # Intentos : %s
                """, failedAttempts);
    }

}


