package org.example.tournamentapp.model;

public record CalculateContext(PlayerDto firstPlayerResult,
                               PlayerDto secondPlayerResult,
                               int firstPlayerEarnedVP,
                               int secondPlayerEarnedVP,
                               int firstPlayerCalculatedTP,
                               int secondPlayerCalculatedTP) {
}
