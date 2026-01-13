package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentUpdatingServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TournamentDataService tournamentDataService;
    @InjectMocks
    private TournamentUpdatingService service;

    @Test
    void updateTourTest() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setCurrentTour(2);

        when(tournamentDataService.getTournamentById(1L)).thenReturn(tournament);

        int result = service.updateCurrentTour(1L);

        assertEquals(3, result);
        assertEquals(3, tournament.getCurrentTour());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void updateFromZeroTest() {
        Tournament tournament = new Tournament();
        tournament.setId(5L);
        tournament.setCurrentTour(0);

        when(tournamentDataService.getTournamentById(5L)).thenReturn(tournament);

        int result = service.updateCurrentTour(5L);

        assertEquals(1, result);
    }

    @Test
    void updateFromMaxTest() {
        Tournament tournament = new Tournament();
        tournament.setId(10L);
        tournament.setCurrentTour(99);

        when(tournamentDataService.getTournamentById(10L)).thenReturn(tournament);

        int result = service.updateCurrentTour(10L);

        assertEquals(100, result);
    }
}


