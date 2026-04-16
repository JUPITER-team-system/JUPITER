package com.management.jupiter.repository;

import com.management.jupiter.models.Cell;
import com.management.jupiter.persistance.Handler;

import java.util.List;

public class CellRepository {
    private static final String FILE_PATH = "data/cells.csv";
    private static final String FILE_NAME = "cells.csv";

    private static Handler handler = new Handler();

    public static  void insertCell(String[] cellArray) {
        List<String[]> cells = handler.read(FILE_NAME);
        cells.add(cellArray);
        handler.write(FILE_NAME, cells);
    }
}
