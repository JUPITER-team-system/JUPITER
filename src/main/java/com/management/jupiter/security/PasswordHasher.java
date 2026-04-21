package com.management.jupiter.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    //Metodo para Hashear antes de ejecutar save
    public static String hash(String plainPassword){
        //12 es el factor de costo
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean check(String plainPassword, String hashedPassword){
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
