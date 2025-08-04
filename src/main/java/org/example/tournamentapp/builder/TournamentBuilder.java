package org.example.tournamentapp.builder;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TournamentBuilder {

    public Tournament getNewTournament(List<PlayerEntity> players, int tourCount) {
        Tournament tournament = new Tournament();
        tournament.setCurrentTour(1);
        tournament.setPlayers(players);
        tournament.setTourDate(LocalDate.now());
        tournament.setTourCount(tourCount);
        return tournament;
    }
}
