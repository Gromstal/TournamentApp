package org.example.tournamentapp.repository;

import org.example.tournamentapp.entity.PairingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PairingRepository extends JpaRepository<PairingEntity,Long> {

    List<PairingEntity> findByCurrentTour(int currentTour);
}
