package com.management.jupiter.repository;

import com.management.jupiter.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(String id);

    void delete(String id);

    void insertCSV(List<String[]> data);
}