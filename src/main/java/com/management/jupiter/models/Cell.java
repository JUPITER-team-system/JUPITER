package com.management.jupiter.models;

import java.util.List;

public class Cell {
    private final int id;
    private final String name;
    private final int clanId;
    private final List<Coder> members;

    public Cell(int id, String name, int clanId, List<Coder> members) {
        this.id = id;
        this.name = name;
        this.clanId = clanId;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getClanId() {
        return clanId;
    }

    public List<Coder> getMembers() {
        return members;
    }


}
