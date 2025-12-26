package org.example.tournamentapp.model;

import java.util.List;

public record TourContext(boolean ended,
                          int updatedTour,
                          List<PairDto> newPairs,
                          List<PlayerDto> players) {

}
