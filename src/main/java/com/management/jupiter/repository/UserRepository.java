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
    private final String FILE_PATH = "users.csv";

    public static User findByEmail(String email) {
        List<User> users = new ArrayList<>(); //Create a user array variable
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/com/management/jupiter/persistance/users.csv"))) {
            String line; //Variable to store the data for each line
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue; //Ignore empty lines

                String[] data = line.split(",");
                if (data.length >= 4 && data[1].equals(email)) {// validate that it meets the fields and that the email exists
                    String rolestr = data[3].trim().toUpperCase(); // get role, delete space and convert to UpperCase
                    var role = Role.valueOf(rolestr);

                    //validate role and create object
                    //Coder
                    if (data.length == 5 && (role == Role.CODER || role == Role.TL)) {
                        try {
                            String clanstr = data[4].trim().toUpperCase(); // get clan, delete space and convert to UpperCase
                            var clan = Clan.valueOf(clanstr);
                            return new Coder(data[0], data[1], data[2], role, clan);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: This clan doesn't exist");
                        }
                    }
                    return new Admin(data[0], data[1], data[2], role);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null; // returns null if the user does not match
    }
}
