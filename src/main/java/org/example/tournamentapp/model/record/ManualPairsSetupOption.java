package org.example.tournamentapp.model.record;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PairsWrapper;

import java.util.List;


public record ManualPairsSetupOption (List<PlayerDto> playerDtoList, PairsWrapper pairsWrapper) {

}
