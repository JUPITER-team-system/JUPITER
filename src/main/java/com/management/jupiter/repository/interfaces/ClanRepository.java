package com.management.jupiter.repository.interfaces;

import com.management.jupiter.models.Clan;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface ClanRepository extends Repository<Clan>  {

    Optional<Clan> findByIdOrName (String value);

    UUID save (Clan clanData, Connection conn) throws SQLException;

    void update (Clan clanData, Connection conn) throws SQLException;

    void delete (String id);

    void removeUser (String id, Connection conn) throws SQLException;

    void addUser (UUID clanID, String userId, Connection conn) throws SQLException;

}
