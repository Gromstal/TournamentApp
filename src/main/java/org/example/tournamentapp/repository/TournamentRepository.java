package org.example.tournamentapp.repository;

import org.example.tournamentapp.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("SELECT t.currentTour FROM Tournament t WHERE t.id = :id")
    int getCurrentTourById(@Param("id") Long id);

    @Query("SELECT t.tourCount FROM Tournament t WHERE t.id = :id")
    int getTourCountById(@Param("id") Long id);

    @Query("SELECT t.tournamentIsEnded FROM Tournament t WHERE t.id = :id")
    boolean getIsEndedById(@Param("id") Long id);

    void deleteById(Long tournamentId);
}
