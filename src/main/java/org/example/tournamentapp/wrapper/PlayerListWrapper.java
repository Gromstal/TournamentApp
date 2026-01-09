package org.example.tournamentapp.wrapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class PlayerListWrapper {
    @NotNull
    @Size(min = 2, message = "Нужно минимум 2 игрока")
    @Valid
    private List<PlayerDto> playerList;
}