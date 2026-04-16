package com.management.jupiter.models;

import java.util.ArrayList;
import java.util.List;

public class Clan {

    private String id;
    private String name;
    private List<Coder> coders;
    private List<Tl> tls;

    public Clan(String id, String name) {
        this.id = id;
        this.name = name;
        this.coders = new ArrayList<>();
        this.tls = new ArrayList<>();
    }

    // ── Getters básicos ──────────────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ── Gestión de Coders ────────────────────────────────────────────────────

    public List<Coder> getCoders() {
        return coders;
    }

    public void addCoder(Coder coder) {
        if (coder != null && !coders.contains(coder)) {
            coders.add(coder);
        }
    }

    public void removeCoder(Coder coder) {
        coders.remove(coder);
    }

    public boolean hasCoders() {
        return !coders.isEmpty();
    }

    // ── Gestión de TLs ───────────────────────────────────────────────────────

    public List<Tl> getTls() {
        return tls;
    }

    public void addTl(Tl tl) {
        if (tl != null && !tls.contains(tl)) {
            tls.add(tl);
        }
    }

    public void removeTl(Tl tl) {
        tls.remove(tl);
    }

    public boolean hasTl(Tl tl) {
        return tls.contains(tl);
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Clan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalCoders=" + coders.size() +
                ", totalTLs=" + tls.size() +
                '}';
    }
}
