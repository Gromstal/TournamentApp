package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentFinishingServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PairingRepository pairingRepository;
    @InjectMocks
    private TournamentFinishingService service;

    @Test
    void deleteAllTest() {
        service.deleteTournament(1L);

        verify(playerRepository, times(1)).deleteOpponentsLinksByTournamentId(1L);
        verify(pairingRepository, times(1)).deleteByTournament_Id(1L);
        verify(playerRepository, times(1)).deleteByTournament_Id(1L);
        verify(tournamentRepository, times(1)).deleteById(1L);
    }

    @Test
    void isEndedTrueTest() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(new Tournament()));
        when(tournamentRepository.getIsEndedById(1L)).thenReturn(true);

        assertTrue(service.isEnded(1L));
    }

    @Test
    void isEndedFalseTest() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(new Tournament()));
        when(tournamentRepository.getIsEndedById(1L)).thenReturn(false);

        assertFalse(service.isEnded(1L));
    }

    @Test
    void isEndedNotFoundTest() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.isEnded(999L));
    }

    @Test
    void setEndedFlagTest() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTournamentIsEnded(false);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        service.saveIsEnded(1L);

        assertTrue(tournament.isTournamentIsEnded());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void setEndedNotFoundTest() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.saveIsEnded(999L));
    }
}


