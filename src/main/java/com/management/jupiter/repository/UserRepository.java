package com.management.jupiter.repository;


import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final String FILE_PATH = "app/com/jupiter/users.csv";

    public static User findByEmail(String email) {
        List<User> users = new ArrayList<>(); //Create a user array variable
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/com/management/jupiter/persistance/users.csv"))) {
            String line; //Variable to store the data for each line
            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;

                String[] data = line.split(",");

                if (data.length >= 4 && data[1].equals(email)) {

                    String rolestr = data[3].trim().toUpperCase();
                    var role = Role.valueOf(rolestr);

                    // ✅ CODER o TL (SIN clan en constructor)
                    if (role == Role.CODER || role == Role.TL) {

                        // 🔹 Si quieres, puedes seguir leyendo el clan pero NO usarlo
                        if (data.length == 5) {
                            String clanstr = data[4].trim().toUpperCase();
                            // Aquí podrías usarlo después en AssignmentService
                        }

                        return new Coder(
                                data[0],
                                data[1],
                                data[2],
                                role
                        );
                    }

                    // ✅ ADMIN
                    return new Admin(
                            data[0],
                            data[1],
                            data[2],
                            role
                    );
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null; // returns null if the user does not match
    }
}
