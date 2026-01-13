package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.record.ManualPairsSetupOption;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManualSetupService {

    private final SavePairingService savePairingService;
    private final CreatingPairingService creatingPairingService;
    private final TournamentDataService tournamentDataService;

    @Transactional
    public void saveManualSetup(Long tournamentId, List<PairDto> pairs) {
        savePairingService.savePairingList(pairs, tournamentId);
        savePairingService.saveOpponentsManualSetup(pairs,tournamentId);
        log.info("Starting manual pairs saved for tournament {}", tournamentId);
    }

    public ManualPairsSetupOption getSetupPairWrapper(Long tournamentId) {
        List<PlayerDto> playerDtoList = tournamentDataService.getPlayersDTO(tournamentId);
        List<PairDto> pairs = creatingPairingService.createManualPairList(playerDtoList);

        return new ManualPairsSetupOption(playerDtoList,new PairsWrapper(pairs));
    }

}
