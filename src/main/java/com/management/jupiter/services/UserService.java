package com.management.jupiter.services;

import com.management.jupiter.models.User;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Admin;
import com.management.jupiter.repository.UserRepository;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;
import com.management.jupiter.models.enums.Role;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    
    // Singleton instance for static methods compatibility
    private static final UserService instance = new UserService();
    
    public UserService() {
        this.userRepository = new AdminRepositoryImpl();
    }
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Busca usuario por email o ID (lógica de negocio)
     */
    public Optional<User> findByEmailOrId(String emailOrId) {
        if (emailOrId == null || emailOrId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String trimmedValue = emailOrId.trim();
        
        // Try to parse as ID first - check if it looks like a UUID
        try {
            // If it contains hyphens, treat as UUID ID
            if (trimmedValue.contains("-")) {
                return userRepository.findById(trimmedValue);
            } else {
                // Try parsing as email
                return userRepository.findByEmail(trimmedValue);
            }
        } catch (Exception e) {
            // If not a number, treat as email
            return userRepository.findByEmail(trimmedValue);
        }
    }
    
    /**
     * Autenticación de usuario
     */
    public User authenticate(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email es requerido");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Contraseña es requerida");
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        
        if (userOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado en el sistema");
        }
        
        User foundUser = userOpt.get();
        
        if (!foundUser.getPassword().equals(password.trim())) {
            throw new Exception("Contraseña incorrecta");
        }
        
        return foundUser;
    }
    
    /**
     * Verificar si email existe
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return userRepository.findByEmail(email.trim()).isPresent();
    }
    
    /**
     * Eliminar usuario por email o ID
     */
    public void deleteUser(String emailOrId) throws Exception {
        Optional<User> userOpt = findByEmailOrId(emailOrId);
        
        if (userOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado: " + emailOrId);
        }
        
        userRepository.delete(userOpt.get().getId());
    }
    
    // ===== MÉTODOS ESTÁTICOS PARA COMPATIBILIDAD =====
    
    /**
     * Login service - Método estático para compatibilidad con código existente
     */
    public static User LoginService(String email, String password) throws Exception {
        return instance.authenticate(email, password);
    }
    
    /**
     * Crear usuarios de prueba - Método estático para compatibilidad
     */
    public static void createTestUsers() {
        AdminRepositoryImpl userRepository = new AdminRepositoryImpl();
        
        try {
            System.out.println("=== Creando usuarios de prueba ===");
            
            // Verificar si ya existen usuarios de prueba
            if (instance.emailExists("admin@test.com")) {
                System.out.println("Los usuarios de prueba ya existen");
                return;
            }
            
            // Crear Admin de prueba
            Admin adminTest = new Admin("admin", "admin@test.com", "123456", Role.ADMIN);
            userRepository.save(adminTest);
            System.out.println("Admin creado: admin@test.com / 123456");
            
            // Crear Coder de prueba
            Coder coderTest = new Coder("coder", "coder@test.com", "123456", Role.CODER);
            userRepository.save(coderTest);
            System.out.println("Coder creado: coder@test.com / 123456");
            
            // Crear TL de prueba
            Tl tlTest = new Tl("teamleader", "tl@test.com", "123456", Role.TL, null);
            userRepository.save(tlTest);
            System.out.println("TL creado: tl@test.com / 123456");
            
            System.out.println("=== Usuarios de prueba creados exitosamente ===");
            System.out.println("Ahora puedes probar el login con estas credenciales");
            
        } catch (Exception e) {
            System.out.println("[ERROR]: Error al crear usuarios de prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
