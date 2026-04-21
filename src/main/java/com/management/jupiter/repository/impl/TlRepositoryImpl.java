package com.management.jupiter.repository.impl;

import com.management.jupiter.models.User;
import com.management.jupiter.repository.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;

public class TlRepositoryImpl implements UserRepository {

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Void save(User user) {

        return null;
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void insertCSV(List<String[]> data) {

    }
}
