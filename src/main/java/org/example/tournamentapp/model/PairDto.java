package org.example.tournamentapp.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PairDto {
    @NotNull
    @Valid
    private PlayerDto firstPlayer;
    @NotNull
    @Valid
    private PlayerDto secondPlayer;
}
