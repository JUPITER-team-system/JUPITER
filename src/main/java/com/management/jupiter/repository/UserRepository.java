package com.management.jupiter.repository;

import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UserRepository {

    private static final String FILE_PATH = "data/users.csv";

    public static User findByEmail(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = line.split(",");
                User user = mapLineToUser(data);
                if (user != null && user.getEmail().equalsIgnoreCase(email.trim())) {
                    return user;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static User findByIdOrEmail(String value) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] data = line.split(",");
                User user = mapLineToUser(data);

                if (user != null &&
                        (user.getEmail().equalsIgnoreCase(value.trim()) ||
                                String.valueOf(user.getId()).equals(value.trim()))) {

                    return user;
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }



    private static User mapLineToUser(String[] data) {
        if (data.length < 5) {
            return null;
        }

        try {
            int id = Integer.parseInt(data[0].trim());
            String username = data[1].trim();
            String email = data[2].trim();
            String password = data[3].trim();
            Role role = Role.valueOf(data[4].trim().toUpperCase());

            if (role == Role.TL) {
                TlType tlType = parseTlType(data);
                return new Tl(id, username, email, password, role, tlType);
            }

            if (role == Role.CODER) {
                return new Coder(id, username, email, password, role);
            }

            return new Admin(id, username, email, password, role);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static TlType parseTlType(String[] data) {
        if (data.length >= 7 && !data[6].trim().isEmpty()) {
            return TlType.valueOf(data[6].trim().toUpperCase());
        }
        if (data.length >= 6 && !data[5].trim().isEmpty()) {
            return TlType.valueOf(data[5].trim().toUpperCase());
        }
        return TlType.PROGRAMACION;
    }
}
