package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.PairDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManualSetupService {

    private final PairingService pairingService;
    private final TournamentService tournamentService;

    public void saveManualSetup(List<PairDto> pairs) {
        Long id = tournamentService.getTournamentIdByTourDate(LocalDate.now());
        pairingService.savePairingList(pairs, id);
        pairingService.saveOpponentsManualSetup(pairs);
    }

}
