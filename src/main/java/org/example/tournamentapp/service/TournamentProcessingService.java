package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.TourContext;
import org.example.tournamentapp.model.record.TournamentContext;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentProcessingService {
    
    private final CreatingPairingService creatingPairingService;
    private final MergePairingService mergePairingService;
    private final CalculateService calculateService;
    private final SavePairingService savePairingService;
    private final TournamentFinishingService tournamentFinishingService;
    private final TournamentUpdatingService tournamentUpdatingService;
    private final TournamentDataService tournamentDataService;
    private final TournamentOpponentHistoryService tournamentOpponentHistoryService;

    @Transactional
    public TourContext processingTournament(Long tournamentId, PairsWrapper pairsWrapper) {

        TournamentContext tournamentContext = tournamentDataService.getTournamentContext(tournamentId);
        List<PairDto> pairs = mergePairingService.getPairingList(tournamentOpponentHistoryService.getOpponentsMap(tournamentId),tournamentId,tournamentContext.currentTour());

        mergePairingService.mergePairs(pairs, pairsWrapper);
        calculateService.calculateFromPairsWrapper(tournamentId,new PairsWrapper(pairs));

        if (tournamentContext.currentTour() == tournamentContext.total()) {
            tournamentFinishingService.saveIsEnded(tournamentId);
            return new TourContext(true, tournamentContext.currentTour(), List.of(), List.of());
        }

        List<PlayerDto> players = tournamentDataService.getPlayersDTO(tournamentId);
        List<PairDto> newPairs = creatingPairingService.createTourPairList(tournamentId,players);
        int updatedTour = tournamentUpdatingService.updateCurrentTour(tournamentContext.tournamentId());
        savePairingService.savePairingList(newPairs, tournamentContext.tournamentId());

        log.info("New pairs for {} tour (Tournament {}) creating and saved successfully", updatedTour, tournamentId);
        log.debug("New pairs: {}", newPairs);
        return new TourContext(false, updatedTour, newPairs, players);
    }
    
}
