package org.example.tournamentapp.repository;

import org.example.tournamentapp.entity.Pairing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PairingRepository extends JpaRepository<Pairing,Long> {
    List<Pairing> findByCurrentTour(int currentTour);
}
