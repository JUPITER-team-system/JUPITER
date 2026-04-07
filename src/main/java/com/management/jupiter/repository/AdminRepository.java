package com.management.jupiter.repository;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.persistance.Handler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class AdminRepository {
    private static final String FILE_PATH = "src/main/java/com/management/jupiter/persistance/users.csv";

    private Handler handler;

    public AdminRepository() {
        handler = new Handler("./app/com/jupiter/src/persistance");
    }

    public void getAllUsers() {
        List<String[]> users = handler.read("info.csv");

        for (String[] user : users) {
            System.out.println("Name: " + user[0] + " Email: " + user[1] + " Rol: " + user[2]);
        }
    }
    public void insertUser(String name, String email, String password, String role){
        //Creo el array que los va a contener.
        List<String[]> users = handler.read("info.csv");

        //lo construyo
        String[] newUser = new String[]{name, email, password, role};

        //Crear nuevo usuario - fila
        users.add(new String[]{name, email, password, role});

        //Lo añadimos al archivo para la persistencia
        handler.write("info.csv", users);
    }
    public void deleteUser(String email){
        //Leemos todos los usuarios.
        List<String[]> users = handler.read("info.csv");

        //Filtramos los que no coincidan con el email para eliminar
        boolean removed = users.removeIf(user -> user[1].equalsIgnoreCase(email));
        if (removed){
            //Reescribo el CSV sin el usuario eliminado.
            handler.write("info.csv", users);
            System.out.println("El usuario eliminado fue: " + email);
        }else {
            System.out.println("Ese usuario no existe");
        }
    }


    public static void save(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String line = mapToLine(user);
            bw.write(line);
            bw.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }
    private static String mapToLine(User user) {
        String base = user.getUsername() + "," +
                user.getEmail() + "," +
                user.getPassword() + "," +
                user.getRole();

        if (user instanceof Coder coder) {
            return base + "," + coder.getClan();
        }
        if (user instanceof Tl tl) {
            return base + "," + tl.getClan();
        }

        return base;
    }
}
