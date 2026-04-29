package com.management.jupiter.services;

import com.management.jupiter.models.Cell;
import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.repository.CellRepository;
import com.management.jupiter.repository.ai.AiProvider;
import com.management.jupiter.repository.interfaces.CellRepositoryInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        if (cellsQuantity <= 0) {
            throw new IllegalArgumentException("The number of cells must be greater than zero.");
        }

        UUID clanId = UUID.fromString(clan.getId());
        List<UUID> createdCellIds = new ArrayList<>();
        List<String> names = aiProvider.generateNames(cellsQuantity, theme);

        for (String name : names) {
            if (CellRepository.existsByName(name)) {
                System.out.println("The cell already exists");
                continue;
            }

            Cell createdCell = new Cell(name, clanId);
            UUID cellId = cellRepository.saveAndReturnId(createdCell);
            createdCellIds.add(cellId);
        }

        if (createdCellIds.isEmpty()) {
            throw new IllegalStateException("No cells were created.");
        }

        assignCodersEquitably(clanId, createdCellIds);
    }

    private void assignCodersEquitably(UUID clanId, List<UUID> cellIds) {
        List<Coder> coders = new ArrayList<>(cellRepository.findCodersByClanId(clanId));
        Collections.shuffle(coders);

        for (int i = 0; i < coders.size(); i++) {
            UUID cellId = cellIds.get(i % cellIds.size());
            cellRepository.assignCoderToCell(coders.get(i).getId(), cellId, clanId);
        }
    }

    public Map<String, List<Coder>> getCodersGroupedByCell(Clan clan) {
        if (clan == null || clan.getId() == null || clan.getId().isBlank()) {
            throw new IllegalStateException("A valid clan is required to view the team.");
        }

        return cellRepository.findCodersGroupedByCell(UUID.fromString(clan.getId()));
    }
}
