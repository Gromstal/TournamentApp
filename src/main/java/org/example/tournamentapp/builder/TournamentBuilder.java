package org.example.tournamentapp.builder;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.exception.DuplicatePlayerException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class TournamentBuilder {

    public Tournament getNewTournament(Set<PlayerEntity> players, int tourCount) {
        validateNoDuplicatePlayers(players);

        Tournament tournament = new Tournament();
        tournament.setCurrentTour(1);
        tournament.setPlayers(players);
        tournament.setTourDate(LocalDate.now());
        tournament.setTourCount(tourCount);
        return tournament;
    }

    private void validateNoDuplicatePlayers(Set<PlayerEntity> players) {
        Set<String> seen = new HashSet<>();

        for (PlayerEntity player : players) {
            String normalizedName = normalize(player.getName(), player.getFaction());

            if (!seen.add(normalizedName)) {
                throw new DuplicatePlayerException(
                        "Player '" + player.getName() + "' (" + player.getFaction() + ") is duplicated in tournament"
                );
            }
        }
    }

    private String normalize(String name, String faction) {
        return normalizeField(name) + "|" + normalizeField(faction);
    }

    private String normalizeField(String value) {
        return value == null
                ? ""
                : value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }
}
