package com.management.jupiter.repository;

import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.persistance.Handler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.management.jupiter.repository.TeamLeaderRepository.FILE_PATH;


public class AdminRepository {

    private Handler handler;

    public AdminRepository() {
        handler = new Handler();
    }

    public void getAllUsers() {
        List<String[]> users = handler.read("info.csv");

        for (String[] user : users) {
            System.out.println("Name: " + user[0] + " Email: " + user[1] + " Rol: " + user[2]);
        }
    }

    public void insertUser(String name, String email, String password, String role) {
        //Creo el array que los va a contener.
        List<String[]> users = handler.read("info.csv");

        //lo construyo
        String[] newUser = new String[]{name, email, password, role};

        users.add(newUser);

        //Lo añadimos al archivo para la persistencia
        handler.write("info.csv", users);
    }

    public void deleteUser(String value) {
        //Leemos todos los usuarios.
        List<String[]> users = handler.read("users.csv");

        //Filtramos los que no coincidan con el email para eliminar
        boolean removed = users.removeIf(user -> user[2].equalsIgnoreCase(value) || user[0].equalsIgnoreCase(value));
        if (removed) {
            handler.write("users.csv", users);
            System.out.println("User delete is: " + value);
        } else {
            System.out.println("User not exist.");
        }
    }

    //Create clan
    public void insertClan(int id, String clanName, String teamLeader, String members) {
        List<String[]> clans = handler.read("clans.csv");

        String[] newClan = new String[]{(String.valueOf(id)), clanName, teamLeader, members}; //Con el valueOf convertimos a String.

        clans.add(newClan);

        handler.write("clans.csv", clans);


    }

    public static void save(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            String line = mapToLine(user);
            bw.newLine();
            bw.write(line);

        } catch (IOException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    private static String mapToLine(User user) {
        String base = user.getId() + "," + user.getUsername() + "," + user.getEmail() + "," + user.getPassword() + "," + user.getRole();

        if (user instanceof Tl tl) {
            return base + ",," + tl.getTlType();
        }

        return base;
    }
}