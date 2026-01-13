package org.example.tournamentapp.service;

import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.ProcessingOption;
import org.example.tournamentapp.model.record.TourContext;
import org.example.tournamentapp.model.record.TournamentContext;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentProcessingServiceTest {

    @Mock
    private CreatingPairingService creatingPairingService;
    @Mock
    private MergePairingService mergePairingService;
    @Mock
    private CalculateService calculateService;
    @Mock
    private SavePairingService savePairingService;
    @Mock
    private TournamentFinishingService tournamentFinishingService;
    @Mock
    private TournamentUpdatingService tournamentUpdatingService;
    @Mock
    private TournamentDataService tournamentDataService;
    @Mock
    private TournamentOpponentHistoryService tournamentOpponentHistoryService;
    @InjectMocks
    private TournamentProcessingService service;

    @Test
    void processLastTourTest() {
        TournamentContext context = new TournamentContext(5, 5, 1L);
        Map<Long, Set<String>> opponentsMap = new HashMap<>();
        List<PairDto> pairs = List.of(new PairDto());

        when(tournamentDataService.getTournamentContext(1L)).thenReturn(context);
        when(tournamentOpponentHistoryService.getOpponentsMap(1L)).thenReturn(opponentsMap);
        when(mergePairingService.getPairingList(opponentsMap, 1L, 5)).thenReturn(pairs);

        TourContext result = service.processingTournament(1L, new PairsWrapper(pairs));

        assertTrue(result.ended());
        verify(tournamentFinishingService, times(1)).saveIsEnded(1L);
        verify(creatingPairingService, never()).createTourPairList(anyLong(), anyList());
    }

    @Test
    void processNextTourTest() {
        TournamentContext context = new TournamentContext(2, 5, 1L);
        Map<Long, Set<String>> opponentsMap = new HashMap<>();
        List<PairDto> currentPairs = List.of(new PairDto());
        List<PairDto> newPairs = List.of(new PairDto(), new PairDto());
        List<PlayerDto> players = List.of(new PlayerDto(), new PlayerDto());

        when(tournamentDataService.getTournamentContext(1L)).thenReturn(context);
        when(tournamentOpponentHistoryService.getOpponentsMap(1L)).thenReturn(opponentsMap);
        when(mergePairingService.getPairingList(opponentsMap, 1L, 2)).thenReturn(currentPairs);
        when(tournamentDataService.getPlayersDTO(1L)).thenReturn(players);
        when(creatingPairingService.createTourPairList(1L, players)).thenReturn(newPairs);
        when(tournamentUpdatingService.updateCurrentTour(1L)).thenReturn(3);

        TourContext result = service.processingTournament(1L, new PairsWrapper(currentPairs));

        assertFalse(result.ended());
        assertEquals(3, result.updatedTour());
        assertEquals(2, result.newPairs().size());
        verify(savePairingService, times(1)).savePairingList(newPairs, 1L);
    }

    @Test
    void processingOptionTest() {
        Map<Long, Set<String>> opponentsMap = new HashMap<>();
        List<PairDto> pairs = List.of(new PairDto());

        when(tournamentDataService.getCurrentTourByTournamentId(1L)).thenReturn(3);
        when(tournamentOpponentHistoryService.getOpponentsMap(1L)).thenReturn(opponentsMap);
        when(mergePairingService.getPairingList(opponentsMap, 1L, 3)).thenReturn(pairs);

        ProcessingOption result = service.getProcessingOption(1L);

        assertEquals(3, result.currentTour());
        assertNotNull(result.wrapper());
        assertEquals(1, result.wrapper().getPairs().size());
    }

    @Test
    void processFirstTourTest() {
        TournamentContext context = new TournamentContext(1, 5, 1L);
        Map<Long, Set<String>> opponentsMap = new HashMap<>();
        List<PairDto> currentPairs = List.of(new PairDto());
        List<PairDto> newPairs = List.of(new PairDto());
        List<PlayerDto> players = List.of(new PlayerDto());

        when(tournamentDataService.getTournamentContext(1L)).thenReturn(context);
        when(tournamentOpponentHistoryService.getOpponentsMap(1L)).thenReturn(opponentsMap);
        when(mergePairingService.getPairingList(opponentsMap, 1L, 1)).thenReturn(currentPairs);
        when(tournamentDataService.getPlayersDTO(1L)).thenReturn(players);
        when(creatingPairingService.createTourPairList(1L, players)).thenReturn(newPairs);
        when(tournamentUpdatingService.updateCurrentTour(1L)).thenReturn(2);

        TourContext result = service.processingTournament(1L, new PairsWrapper(currentPairs));

        assertFalse(result.ended());
        assertEquals(2, result.updatedTour());
    }
}


