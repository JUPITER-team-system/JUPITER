package com.management.jupiter.controllers;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.ai.AiProvider;
import com.management.jupiter.repository.ai.GeminiProvider;
import com.management.jupiter.services.CellServices;

public class CellController {

    private final CellServices cellServices;

    public CellController(CellServices cellServices) {
        this.cellServices = cellServices;
    }

    public void createCell(int cellsQuantity, String theme) {
        try {
            cellServices.createCell(cellsQuantity, theme);
            System.out.println("Cells created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void assignateCoderToCell() {

    }
}
