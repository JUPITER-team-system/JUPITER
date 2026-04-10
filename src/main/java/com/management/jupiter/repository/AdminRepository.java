package com.management.jupiter.repository;

import com.management.jupiter.persistance.Handler;

import java.util.List;

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

    public void insertUser(String name, String email, String password, String role){
        //Lee existentes
        List<String[]> users = handler.read("info.csv");

        String[] newUser = new String[]{name, email, password, role};

        users.add(newUser);

        handler.write("info.csv", users);
    }

    public void deleteUser(String email){
        List<String[]> users = handler.read("info.csv");

        boolean removed = users.removeIf(user -> user[1].equalsIgnoreCase(email));
        if (removed){
            handler.write("info.csv", users);
            System.out.println("User delete is: " + email);
        } else {
            System.out.println("User not exist.");
        }
    }
    //Create clan
    public void insertClan(int id, String clanName,String teamLeader, String members){
        List<String[]> clans = handler.read("clans.csv");

        String[] newClan = new String[]{(String.valueOf(id)) , clanName,teamLeader, members}; //Con el valueOf convertimos a String.

        clans.add(newClan);

        handler.write("clans.csv", clans);
}
}