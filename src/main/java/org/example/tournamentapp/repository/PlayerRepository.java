package org.example.tournamentapp.repository;

import org.example.tournamentapp.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity,Long> {

    Optional<PlayerEntity> findByName(String name);

    List<PlayerEntity> findAllByTournamentId(Long tournamentId);
}
