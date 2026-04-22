package com.management.jupiter.controllers;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.ai.AiProvider;
import com.management.jupiter.repository.ai.GeminiProvider;
import com.management.jupiter.services.CellServices;

public class CellController {
    AiProvider aiProvider = new GeminiProvider();
    CellServices cellServices = new CellServices(aiProvider);

    public void createCell() {
        try {
            cellServices.createCell();
            System.out.println("Clan created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void assignateCoderToCell() {

    }
}
