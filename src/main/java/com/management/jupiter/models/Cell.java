package com.management.jupiter.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cell {
    private String id;          // UUID como String — coincide con el tipo de la columna en PostgreSQL
    private final String name;
    private final UUID clanId;
    private final List<Coder> members;

    public Cell(String name, UUID clanId) {
        this.name = name.toUpperCase();
        this.clanId = clanId;
        this.members = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public UUID getClanId() {
        return clanId;
    }

    public List<Coder> getMembers() {
        return members;
    }
}
