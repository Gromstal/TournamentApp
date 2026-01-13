package org.example.tournamentapp.model.record;

import org.example.tournamentapp.model.PlayerDto;

public record CalculateContext(PlayerDto firstPlayerResult,
                               PlayerDto secondPlayerResult,
                               int firstPlayerEarnedVP,
                               int secondPlayerEarnedVP,
                               int firstPlayerCalculatedTP,
                               int secondPlayerCalculatedTP) {
}
