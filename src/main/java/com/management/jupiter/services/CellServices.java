package com.management.jupiter.services;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.CellRepository;
import com.management.jupiter.repository.ai.AiProvider;

import javax.management.RuntimeErrorException;
import java.util.List;

public class CellServices {
    private final AiProvider aiProvider;

    public CellServices(AiProvider aiProvider) {
        this.aiProvider = aiProvider;
    }

    public void createCell() {
        int idClan = 2;
        List<String> names = aiProvider.generateNames(4,"planetas");
        for (String name : names) {
            System.out.println("Existe? -> " + CellRepository.existsByName(name));
            if (CellRepository.existsByName(name)) {
                System.out.println(("The clan already exists"));
                continue;
            }
            int id = Handler.nextId("data/cells.csv");
            Cell createdCell = new Cell(id, name, idClan);
            String[] cellArray = new String[]{String.valueOf(createdCell.getId()), createdCell.getName()};
            CellRepository.insertCell(cellArray);
        }
    }
}
