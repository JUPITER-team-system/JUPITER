package com.management.jupiter.repository;

import com.management.jupiter.models.User;
import com.management.jupiter.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User> {
    Optional<User> findByEmail(String email);
}