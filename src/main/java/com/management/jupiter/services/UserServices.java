package com.management.jupiter.services;
import com.management.jupiter.models.User;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Admin;
import com.management.jupiter.repository.UserRepository;
import com.management.jupiter.impl.UserRepositoryImpl;
import com.management.jupiter.models.enums.Role;

import java.util.Optional;

public class UserServices {


    public static User LoginService(String email, String password) throws Exception {
        // Crear instancia del repositorio de usuarios
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        
        // Buscar usuario por email en la base de datos
        Optional<User> user = userRepository.findByEmail(email);
        
        // Paso 1: Verificar si el usuario existe
        // Optional.isEmpty() es la forma correcta de verificar si no hay valor
        if (user.isEmpty()) {
            throw new Exception("Usuario no encontrado en el sistema");
        }
        

        User foundUser = user.get();

        if (!foundUser.getPassword().equals(password)) {
            throw new Exception("Contraseña incorrecta");
        }
        
        // Paso 4: Login exitoso - retornar usuario autenticado
        return foundUser;
    }

    public static void createTestUsers() {
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        
        try {
            System.out.println("=== Creando usuarios de prueba ===");
            
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
