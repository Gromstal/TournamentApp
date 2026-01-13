package org.example.tournamentapp.service;

import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.ManualPairsSetupOption;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManualSetupServiceTest {

    @Mock
    private SavePairingService savePairingService;
    @Mock
    private CreatingPairingService creatingPairingService;
    @Mock
    private TournamentDataService tournamentDataService;
    @InjectMocks
    private ManualSetupService service;

    @Test
    void manualSaveTest() {
        List<PairDto> pairs = List.of(new PairDto());

        service.saveManualSetup(1L, pairs);

        verify(savePairingService, times(1)).savePairingList(pairs, 1L);
        verify(savePairingService, times(1)).saveOpponentsManualSetup(pairs, 1L);
    }

    @Test
    void pairWrapperTest() {
        List<PlayerDto> players = List.of(new PlayerDto(), new PlayerDto());
        List<PairDto> pairs = List.of(new PairDto());

        when(tournamentDataService.getPlayersDTO(1L)).thenReturn(players);
        when(creatingPairingService.createManualPairList(players)).thenReturn(pairs);

        ManualPairsSetupOption result = service.getSetupPairWrapper(1L);

        assertNotNull(result);
        assertEquals(players, result.playerDtoList());
        assertNotNull(result.pairsWrapper());
        verify(tournamentDataService, times(1)).getPlayersDTO(1L);
        verify(creatingPairingService, times(1)).createManualPairList(players);
    }

    @Test
    void manualEmptyTest() {
        List<PairDto> emptyPairs = List.of();

        service.saveManualSetup(2L, emptyPairs);

        verify(savePairingService, times(1)).savePairingList(emptyPairs, 2L);
        verify(savePairingService, times(1)).saveOpponentsManualSetup(emptyPairs, 2L);
    }
}


