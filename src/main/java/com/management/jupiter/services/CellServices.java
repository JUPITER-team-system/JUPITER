package com.management.jupiter.services;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.CellRepository;
import com.management.jupiter.repository.ai.AiProvider;
import com.management.jupiter.repository.impl.CellRepositoryInterfaceImpl;

import javax.management.RuntimeErrorException;
import java.util.List;

public class CellServices {
    private final AiProvider aiProvider;
    private final CellRepositoryInterfaceImpl cellRepository;

    public CellServices(AiProvider aiProvider, CellRepositoryInterfaceImpl cellRepository) {
        this.aiProvider = aiProvider;
        this.cellRepository = cellRepository;
    }

    public void createCell(int cellsQuantity, String theme) {
        List<String> names = aiProvider.generateNames(cellsQuantity, theme);
        for (String name : names) {
            if (CellRepository.existsByName(name)) {
                System.out.println(("The clan already exists"));
                continue;
            }
            //int id = Handler.nextId("data/cells.csv");
            Cell createdCell = new Cell(name);
            cellRepository.save(createdCell);
//            String[] cellArray = new String[]{String.valueOf(createdCell.getId()), createdCell.getName()};
//            CellRepository.insertCell(cellArray);
        }
    }
}
