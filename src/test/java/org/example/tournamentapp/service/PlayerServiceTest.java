package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @InjectMocks
    private PlayerService service;

    @Test
    void savePlayerTest() {
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);

        service.savePlayer(player);

        verify(playerRepository, times(1)).save(player);
    }

    @Test
    void opponentsBidirectionalTest() {
        Long playerId = 1L;
        Long opponentId = 2L;

        service.saveOpponents(playerId, opponentId);

        verify(playerRepository, times(1)).saveOpponents(playerId, opponentId);
        verify(playerRepository, times(1)).saveOpponents(opponentId, playerId);
    }

    @Test
    void playerByIdTest() {
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        PlayerEntity result = service.getPlayerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void playerByIdNotFoundTest() {
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(org.example.tournamentapp.exception.PlayerNotFoundException.class,
                () -> service.getPlayerById(999L));
    }

    @Test
    void playersMapTest() {
        PlayerEntity p1 = new PlayerEntity();
        p1.setId(1L);
        PlayerEntity p2 = new PlayerEntity();
        p2.setId(2L);

        when(playerRepository.findAllByTournamentId(1L)).thenReturn(List.of(p1, p2));

        Map<Long, PlayerEntity> result = service.getPlayers(1L);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
    }

    @Test
    void playersEmptyTournamentTest() {
        when(playerRepository.findAllByTournamentId(999L)).thenReturn(List.of());

        Map<Long, PlayerEntity> result = service.getPlayers(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void entityReferenceTest() {
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        when(playerRepository.getReferenceById(1L)).thenReturn(player);

        PlayerEntity result = service.getEntityReference(1L);

        assertNotNull(result);
        verify(playerRepository, times(1)).getReferenceById(1L);
    }
}


