package com.management.jupiter.controllers;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Tl;
import com.management.jupiter.services.ClanService;

import java.util.*;

public class ClanController {

    private final ClanService service;

    public ClanController (ClanService service) {
        this.service = service;
    }

    public List<Clan> readAll () {
        return service.readAll();
    }

    public Optional<Clan> readIdOrName(String value) {

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException("The id or name can't be empty");

        }

        return service.readIdOrName(value);

    }

    public void createClan (String name, String desc) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException("The name can't be empty");

        }

        service.add(new Clan(null, name, desc));

    }

    public void deleteClan (String value) {

        if (value == null || value.isBlank()){

            throw new IllegalArgumentException("The id or name can't be empty");

        }

        service.delete(value);

    }

    public void updateClan (String id, String name, String desc, List<Coder> coders, List<Tl> tls)  {

        if (id == null || id.isBlank()) {

            throw new IllegalArgumentException("The id can't be empty");

        }

        Clan clanUpdate = new Clan(id, name, desc);
        clanUpdate.setCoders(coders);
        clanUpdate.setTls(tls);

        service.edit(clanUpdate);

    }

}
