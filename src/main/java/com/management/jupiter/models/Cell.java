package com.management.jupiter.models;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private int id;
    private final String name;
    private int clanId;
    private List<Coder> members;

    public Cell(String name) {
        this.name = name.toUpperCase();
        this.clanId = clanId;
        //this.members = members;
        this.members = new ArrayList<>();

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
