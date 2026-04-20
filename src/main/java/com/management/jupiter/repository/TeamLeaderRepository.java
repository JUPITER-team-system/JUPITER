package com.management.jupiter.repository;

import com.management.jupiter.models.Tl;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Repositorio respaldado por users.csv para TLs.
 */
public class TeamLeaderRepository {

    public static final String FILE_PATH = "data/users.csv";
    private final ClanRepository clanRepo;

    public TeamLeaderRepository (ClanRepository clanRepo) {
        this.clanRepo = clanRepo;
    }

    public void save(Tl tl) {
        Path path = Path.of(FILE_PATH);

        try {
            List<String> lines = Files.exists(path)
                    ? Files.readAllLines(path)
                    : new ArrayList<>();
            boolean updated = false;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line == null || line.isBlank()) {
                    continue;
                }

                String[] data = line.split(",", -1);
                Tl existingTl = mapLineToTl(data);
                if (existingTl != null && Objects.equals(existingTl.getId(), tl.getId())) {
                    lines.set(i, mapTlToLine(tl));
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                lines.add(mapTlToLine(tl));
            }

            Files.write(path, lines);
        } catch (IOException e) {
            throw new RuntimeException("Error saving TL to CSV", e);
        }
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

    public Tl findById(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Tl tl = mapLineToTl(line.split(","));
                if (tl != null && Objects.equals(tl.getId(), id)) {
                    return tl;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading TL from CSV", e);
        }

        return null;
    }

    public void delete(String id) {
    }

    private Tl mapLineToTl(String[] data) {
        if (data.length < 5) {
            return null;
        }

        try {
            String id = data[0].trim();
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

            Tl tl = new Tl(id, username, email, password, role, tlType);
            loadAssignedClans(tl, data);
            return tl;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void loadAssignedClans(Tl tl, String[] data) {
        if (data.length < 6 || data[5].trim().isEmpty()) {
            return;
        }

        Arrays.stream(data[5].split("\\|"))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .forEach(name -> tl.addClan(new Clan(clanRepo.findByName(name), name,"")));
    }


    private String mapTlToLine(Tl tl) {
        String clans = tl.getClans().stream()
                .map(Clan::getName)
                .distinct()
                .collect(Collectors.joining("|"));

        return tl.getId() + "," +
                tl.getUsername() + "," +
                tl.getEmail() + "," +
                tl.getPassword() + "," +
                tl.getRole() + "," +
                clans + "," +
                tl.getTlType();
    }
}
