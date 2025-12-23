package org.example.tournamentapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PairDto {
    private PlayerDto firstPlayer;
    private PlayerDto secondPlayer;
}
