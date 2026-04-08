package com.management.jupiter.repository;

import com.management.jupiter.models.Tl;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio respaldado por users.csv para TLs.
 */
public class TeamLeaderRepository {

    private static final String FILE_PATH = "src/main/java/com/management/jupiter/persistance/users.csv";

    public void save(Tl tl) {
    }

    public List<Tl> findAll() {
        List<Tl> tls = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Tl tl = mapLineToTl(line.split(","));
                if (tl != null) {
                    tls.add(tl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading TLs from CSV", e);
        }

        return tls;
    }

    public Tl findById(int id) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Tl tl = mapLineToTl(line.split(","));
                if (tl != null && tl.getId() == id) {
                    return tl;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading TL from CSV", e);
        }

        return null;
    }

    public void delete(int id) {
    }

    private Tl mapLineToTl(String[] data) {
        if (data.length < 5) {
            return null;
        }

        try {
            int id = Integer.parseInt(data[0].trim());
            String username = data[1].trim();
            String email = data[2].trim();
            String password = data[3].trim();
            Role role = Role.valueOf(data[4].trim().toUpperCase());

            if (role != Role.TL) {
                return null;
            }

            TlType tlType = TlType.PROGRAMACION;
            if (data.length >= 7 && !data[6].trim().isEmpty()) {
                tlType = TlType.valueOf(data[6].trim().toUpperCase());
            } else if (data.length >= 6 && !data[5].trim().isEmpty()) {
                tlType = TlType.valueOf(data[5].trim().toUpperCase());
            }

            return new Tl(id, username, email, password, role, tlType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
