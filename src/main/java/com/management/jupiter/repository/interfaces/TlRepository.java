package com.management.jupiter.repository.interfaces;
import com.management.jupiter.models.Tl;

import java.util.List;
import java.util.Optional;

public interface TlRepository extends Repository<Tl, Void>{

    @Override
    Void save(Tl tl);

    @Override
    List<Tl> getAll();

    @Override
    Optional<Tl> findById(String id);

    @Override
    void update(Tl tl);
}
