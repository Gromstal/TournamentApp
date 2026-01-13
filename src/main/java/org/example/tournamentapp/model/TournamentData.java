package org.example.tournamentapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TournamentData {
    private Long id;
    private List<PlayerDto> players;
}
