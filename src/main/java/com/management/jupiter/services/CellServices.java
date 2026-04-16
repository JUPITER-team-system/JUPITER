package com.management.jupiter.services;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.Handler;
import com.management.jupiter.repository.CellRepository;

public class CellServices {
    public void createCell(String name, int idClan) {
        try {
            int id = Handler.nextId("data/cells.csv");
            Cell createdCell = new Cell(id, name, idClan);
            String[] cellArray = new String[]{String.valueOf(createdCell.getId()), createdCell.getName()};
            CellRepository.insertCell(cellArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
