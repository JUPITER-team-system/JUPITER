package com.management.jupiter.repository.interfaces;

import java.util.List;
import java.util.Optional;

public  interface Repository<T, R>{
    R save(T t);
    List<T> getAll();
    Optional<T> findById(String id);
    void update(T t);
}