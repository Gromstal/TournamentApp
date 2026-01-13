package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.model.TournamentHistory;
import org.example.tournamentapp.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentHistoryServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @InjectMocks
    private TournamentHistoryService service;

    @Test
    void historyListTest() {
        Tournament t1 = new Tournament();
        t1.setId(1L);
        t1.setTourDate(LocalDate.of(2024, 1, 1));

        Tournament t2 = new Tournament();
        t2.setId(2L);
        t2.setTourDate(LocalDate.of(2024, 2, 1));

        when(tournamentRepository.findAll()).thenReturn(List.of(t1, t2));

        List<TournamentHistory> result = service.getTournamentHistoryList();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void historyEmptyTest() {
        when(tournamentRepository.findAll()).thenReturn(List.of());

        List<TournamentHistory> result = service.getTournamentHistoryList();

        assertTrue(result.isEmpty());
    }

    @Test
    void historySingleTest() {
        Tournament t = new Tournament();
        t.setId(99L);
        t.setTourDate(LocalDate.now());

        when(tournamentRepository.findAll()).thenReturn(List.of(t));

        List<TournamentHistory> result = service.getTournamentHistoryList();

        assertEquals(1, result.size());
        assertEquals(99L, result.get(0).getId());
    }
}


