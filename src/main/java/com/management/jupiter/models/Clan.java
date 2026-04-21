package com.management.jupiter.models;

import com.management.jupiter.models.enums.TlType;

import java.util.ArrayList;
import java.util.List;

public class Clan {

    private String id;
    private String name;
    private String description;
    private List<Coder> coders;
    private List<Tl> tls;

    public Clan(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
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

        if (tl == null){
            return;
        }

        if (tls.contains(tl)){
            throw new IllegalStateException("The TL already exist in the clan");
        }

        long tlCount = tls.stream().filter(t -> t.getTlType() == tl.getTlType()).count();

        int limit = (tl.getTlType().toString().equalsIgnoreCase("PROGRAMACION")) ? 1 : 2;

        if (tlCount >= limit){
            throw new IllegalStateException("Limit of tls of " + tl.getTlType() + "reached");
        }

        this.tls.add(tl);

    }

    public void removeTl(Tl tl) {
        tls.remove(tl);
    }

    public boolean hasTl(Tl tl) {
        return tls.contains(tl);
    }

    public Tl clanTl () {

        return this.tls.stream()
                .filter(t -> t.getTlType() == TlType.PROGRAMACION)
                .findFirst()
                .orElse(null);

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

    public void setCoders(List<Coder> coders) {
        this.coders = (coders != null) ? coders : new ArrayList<>();
    }

    public void setTls(List<Tl> tls) {
        this.tls = (tls != null) ? tls : new ArrayList<>();
    }
}
