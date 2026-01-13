package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.TournamentContext;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentDataServiceTest {

    @Mock
    private TournamentOpponentHistoryService tournamentOpponentHistoryService;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerMapper playerMapper;
    @InjectMocks
    private TournamentDataService service;

    @Test
    void tournamentByIdTest() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        Tournament result = service.getTournamentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void tournamentNotFoundTest() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.getTournamentById(999L));
    }

    @Test
    void currentTourTest() {
        when(tournamentRepository.getCurrentTourById(1L)).thenReturn(3);

        int result = service.getCurrentTourByTournamentId(1L);

        assertEquals(3, result);
    }

    @Test
    void tournamentContextTest() {
        when(tournamentRepository.getCurrentTourById(1L)).thenReturn(2);
        when(tournamentRepository.getTourCountById(1L)).thenReturn(5);

        TournamentContext result = service.getTournamentContext(1L);

        assertEquals(2, result.currentTour());
        assertEquals(5, result.total());
        assertEquals(1L, result.tournamentId());
    }

    @Test
    void playersDtoMapTest() {
        Map<Long, Set<String>> opponentsMap = Map.of(1L, Set.of("Opponent"));
        PlayerEntity entity = new PlayerEntity();
        entity.setId(1L);
        List<PlayerEntity> entities = List.of(entity);
        List<PlayerDto> dtos = List.of(new PlayerDto());

        when(playerRepository.findAllByTournamentId(1L)).thenReturn(entities);
        when(tournamentOpponentHistoryService.getOpponentsMap(1L)).thenReturn(opponentsMap);
        when(playerMapper.toDtoList(opponentsMap, entities)).thenReturn(dtos);

        List<PlayerDto> result = service.getPlayersDTO(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(playerMapper, times(1)).toDtoList(opponentsMap, entities);
    }

    @Test
    void playersEmptyTest() {
        when(playerRepository.findAllByTournamentId(999L)).thenReturn(List.of());

        List<PlayerEntity> result = service.getAllPlayersByTournamentId(999L);

        assertTrue(result.isEmpty());
    }
}


