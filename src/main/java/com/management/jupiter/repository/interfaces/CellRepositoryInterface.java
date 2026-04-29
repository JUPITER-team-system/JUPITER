package com.management.jupiter.repository.interfaces;

import com.management.jupiter.models.Cell;
import com.management.jupiter.models.Coder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CellRepositoryInterface extends  Repository<Cell, Void> {
    UUID saveAndReturnId(Cell cell);
    List<Coder> findCodersByClanId(UUID clanId);
    Map<String, List<Coder>> findCodersGroupedByCell(UUID clanId);
    void assignCoderToCell(String coderId, UUID cellId, UUID clanId);
}
