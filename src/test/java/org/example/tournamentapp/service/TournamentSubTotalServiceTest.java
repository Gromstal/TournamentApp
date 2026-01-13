package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.TournamentSubTotalOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentSubTotalServiceTest {

    @Mock
    private SortingPlayerService sortingPlayerService;
    @Mock
    private TournamentDataService tournamentDataService;
    @InjectMocks
    private TournamentSubTotalService service;

    @Test
    void subTotalDataTest() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setCurrentTour(3);

        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("Player1").build(),
                PlayerDto.builder().name("Player2").build()
        );

        when(tournamentDataService.getTournamentById(1L)).thenReturn(tournament);
        when(tournamentDataService.getPlayersDTO(1L)).thenReturn(players);
        when(sortingPlayerService.getSortedPlayerList(players)).thenReturn(players);

        TournamentSubTotalOption result = service.getTournamentSubTotalOption(1L);

        assertEquals(3, result.currentTour());
        assertNotNull(result.wrapper());
        assertEquals(2, result.wrapper().getPlayerList().size());
    }

    @Test
    void subTotalFirstTest() {
        Tournament tournament = new Tournament();
        tournament.setId(5L);
        tournament.setCurrentTour(1);

        when(tournamentDataService.getTournamentById(5L)).thenReturn(tournament);
        when(tournamentDataService.getPlayersDTO(5L)).thenReturn(List.of());
        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(List.of());

        TournamentSubTotalOption result = service.getTournamentSubTotalOption(5L);

        assertEquals(1, result.currentTour());
    }

    @Test
    void subTotalEmptyTest() {
        Tournament tournament = new Tournament();
        tournament.setId(10L);
        tournament.setCurrentTour(2);

        when(tournamentDataService.getTournamentById(10L)).thenReturn(tournament);
        when(tournamentDataService.getPlayersDTO(10L)).thenReturn(List.of());
        when(sortingPlayerService.getSortedPlayerList(List.of())).thenReturn(List.of());

        TournamentSubTotalOption result = service.getTournamentSubTotalOption(10L);

        assertTrue(result.wrapper().getPlayerList().isEmpty());
    }
}


