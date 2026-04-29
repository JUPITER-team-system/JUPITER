package com.management.jupiter.controllers;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.Cell;
import com.management.jupiter.services.CellServices;
import com.management.jupiter.repository.impl.CellRepositoryInterfaceImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class CellController {

    private final CellServices cellServices;
    private final CellRepositoryInterfaceImpl cellRepository;

    public CellController(CellServices cellServices) {
        this.cellServices = cellServices;
        this.cellRepository = new CellRepositoryInterfaceImpl();
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

    public Map<String, List<Coder>> getCodersGroupedByCell(Clan clan) {
        return cellServices.getCodersGroupedByCell(clan);
    }
    
    public List<Cell> getCellsByClan(Clan clan) {
        try {
            if (clan == null || clan.getId() == null) {
                return List.of();
            }
            UUID clanId = UUID.fromString(clan.getId());
            return cellRepository.findByClanId(clanId);
        } catch (Exception e) {
            System.out.println("Error getting cells by clan: " + e.getMessage());
            return List.of();
        }
    }
    
    public void deleteCell(String cellId) {
        try {
            cellRepository.deleteCellById(cellId);
            System.out.println("Cell deleted successfully");
        } catch (Exception e) {
            System.out.println("Error deleting cell: " + e.getMessage());
            throw new RuntimeException("Failed to delete cell: " + e.getMessage());
        }
    }
}
