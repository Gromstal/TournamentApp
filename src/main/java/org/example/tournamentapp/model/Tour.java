package org.example.tournamentapp.model;

import java.util.List;

public record Tour(boolean ended,
                   int updatedTour,
                   List<PairDto> newPairs,
                   List<PlayerDto> players) {

}
