package org.example.tournamentapp.repository;

import org.example.tournamentapp.entity.PairingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PairingRepository extends JpaRepository<PairingEntity,Long> {

    List<PairingEntity> findByTournament_IdAndCurrentTour(Long tournamentId, int currentTour);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PairingEntity pr where pr.tournament.id = :tournamentId")
    void deleteByTournament_Id(Long tournamentId);
}
