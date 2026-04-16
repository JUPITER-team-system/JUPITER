package com.management.jupiter.repository;

import com.management.jupiter.models.User;
import java.util.Optional;

public  interface UserRepository extends Repository<User> {
    public  Optional<User> findByEmail(String email);
    public User findByEmailorId(String emailOrId);
}