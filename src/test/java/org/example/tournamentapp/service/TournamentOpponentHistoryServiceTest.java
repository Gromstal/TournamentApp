package org.example.tournamentapp.service;

import org.example.tournamentapp.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentOpponentHistoryServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @InjectMocks
    private TournamentOpponentHistoryService service;

    @Test
    void opponentsMapTest() {
        List<Object[]> rows = List.of(
                new Object[]{1L, "Opponent1"},
                new Object[]{1L, "Opponent2"},
                new Object[]{2L, "Opponent3"}
        );

        when(playerRepository.findOpponentNames(1L)).thenReturn(rows);

        Map<Long, Set<String>> result = service.getOpponentsMap(1L);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
        assertEquals(2, result.get(1L).size());
        assertTrue(result.get(1L).contains("Opponent1"));
        assertTrue(result.get(1L).contains("Opponent2"));
        assertEquals(1, result.get(2L).size());
        assertTrue(result.get(2L).contains("Opponent3"));
    }

    @Test
    void opponentsEmptyTest() {
        when(playerRepository.findOpponentNames(999L)).thenReturn(List.of());

        Map<Long, Set<String>> result = service.getOpponentsMap(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void opponentsSingleTest() {
        List<Object[]> rows = List.of(
                new Object[]{1L, "OppA"},
                new Object[]{2L, "OppB"},
                new Object[]{3L, "OppC"}
        );

        when(playerRepository.findOpponentNames(5L)).thenReturn(rows);

        Map<Long, Set<String>> result = service.getOpponentsMap(5L);

        assertEquals(3, result.size());
        assertEquals(1, result.get(1L).size());
        assertEquals(1, result.get(2L).size());
        assertEquals(1, result.get(3L).size());
    }

    @Test
    void opponentsDuplicatesTest() {
        List<Object[]> rows = List.of(
                new Object[]{1L, "Opponent1"},
                new Object[]{1L, "Opponent1"}
        );

        when(playerRepository.findOpponentNames(1L)).thenReturn(rows);

        Map<Long, Set<String>> result = service.getOpponentsMap(1L);

        assertEquals(1, result.get(1L).size());
        assertTrue(result.get(1L).contains("Opponent1"));
    }
}


