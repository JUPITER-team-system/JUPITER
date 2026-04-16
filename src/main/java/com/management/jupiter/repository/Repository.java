package com.management.jupiter.repository;

import java.util.List;
import java.util.Optional;

public  interface Repository<T>{
    void save(T t);
    List<T> getAll();
    Optional<T> findById(String id);
    void update(T t);
    void delete(String id);
}
