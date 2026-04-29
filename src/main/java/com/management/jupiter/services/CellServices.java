package com.management.jupiter.services;

import com.management.jupiter.models.Cell;
import com.management.jupiter.models.Clan;
import com.management.jupiter.repository.CellRepository;
import com.management.jupiter.repository.ai.AiProvider;
import com.management.jupiter.repository.interfaces.CellRepositoryInterface;

import java.util.List;

public class CellServices {
    private final AiProvider aiProvider;
    private final CellRepositoryInterface cellRepository;

    public CellServices(AiProvider aiProvider, CellRepositoryInterface cellRepository) {
        this.aiProvider = aiProvider;
        this.cellRepository = cellRepository;
    }

    public void createCell(int cellsQuantity, String theme, Clan clan) {
        if (clan == null || clan.getId() == null || clan.getId().isBlank()) {
            throw new IllegalStateException("A valid clan is required to create cells.");
        }

        List<String> names = aiProvider.generateNames(cellsQuantity, theme);
        for (String name : names) {
            if (CellRepository.existsByName(name)) {
                System.out.println("The cell already exists");
                continue;
            }
            System.out.println(clan.getId());
            Cell createdCell = new Cell(name, java.util.UUID.fromString(clan.getId()));
            cellRepository.save(createdCell);
        }
    }
}
