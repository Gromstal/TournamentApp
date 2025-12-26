package org.example.tournamentapp.repository;

import org.example.tournamentapp.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;


public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Optional<Tournament> getTournamentByTourDate(LocalDate tourDate);

    @Query("SELECT t.id FROM Tournament t WHERE t.tourDate = :tourDate")
    Long getTournamentIdByTourDate(@Param("tourDate") LocalDate tourDate);

    @Query("SELECT t.currentTour FROM Tournament t WHERE t.id = :id")
    int getCurrentTourById(@Param("id") Long id);

    @Query("SELECT t.currentTour FROM Tournament t WHERE t.tourDate = :tourDate")
    int getCurrentTourByTourDate(@Param("tourDate") LocalDate tourDate);

    @Query("SELECT t.tourCount FROM Tournament t WHERE t.tourDate = :tourDate")
    int getTourCountByTourDate(@Param("tourDate") LocalDate tourDate);

    @Query("SELECT t.tournamentIsEnded FROM Tournament t WHERE t.tourDate = :tourDate")
    boolean getIsEndedCountByTourDate(@Param("tourDate") LocalDate tourDate);

}
