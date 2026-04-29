package com.management.jupiter.controllers;

import com.management.jupiter.models.Clan;
import com.management.jupiter.services.CellServices;

public class CellController {

    private final CellServices cellServices;

    public CellController(CellServices cellServices) {
        this.cellServices = cellServices;
    }

    public void createCell(int cellsQuantity, String theme, Clan clan) {
        try {
            cellServices.createCell(cellsQuantity, theme, clan);
            System.out.println("Cells created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void assignateCoderToCell() {

    }
}
