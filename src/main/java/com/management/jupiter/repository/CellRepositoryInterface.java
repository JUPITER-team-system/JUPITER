package com.management.jupiter.repository;

import com.management.jupiter.models.Cell;
import com.management.jupiter.models.User;

import java.util.List;
import java.util.Optional;

public interface CellRepositoryInterface extends Repository<Cell>{

    Optional<User> findById(String id);

    void delete(String id);

    void insertCSV(List<String[]> data);
}
