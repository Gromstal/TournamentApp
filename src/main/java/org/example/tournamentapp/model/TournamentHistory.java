package org.example.tournamentapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TournamentHistory {
    private Long id;
    private LocalDate date;
}
