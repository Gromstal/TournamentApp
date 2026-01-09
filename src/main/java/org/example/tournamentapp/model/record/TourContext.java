package org.example.tournamentapp.model.record;

import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;

import java.util.List;

public record TourContext(boolean ended,
                          int updatedTour,
                          List<PairDto> newPairs,
                          List<PlayerDto> players) {

}
