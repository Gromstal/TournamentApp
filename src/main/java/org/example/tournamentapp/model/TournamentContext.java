package org.example.tournamentapp.model;

public record TournamentContext(int currentTour,
                                int total,
                                Long tournamentId) {
}
