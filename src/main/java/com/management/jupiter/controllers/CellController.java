package com.management.jupiter.controllers;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.services.CellServices;

public class CellController {
    CellServices cellServices = new CellServices();
    public void createCell(String name, int clanId) {
        try{
            cellServices.createCell(name, clanId);
            System.out.println("Clan created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void assignateCoderToCell(){

    }
}
