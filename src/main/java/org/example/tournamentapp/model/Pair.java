package org.example.tournamentapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tournamentapp.entity.Tournament;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pair {
    private Player firstPlayer;
    private Player secondPlayer;
}
