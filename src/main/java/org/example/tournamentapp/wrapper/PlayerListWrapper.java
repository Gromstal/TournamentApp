package org.example.tournamentapp.wrapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tournamentapp.model.PlayerDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerListWrapper {
    @NotNull
    @Size(min = 2, message = "Need 2 or more players")
    @Valid
    private List<PlayerDto> playerList;
}