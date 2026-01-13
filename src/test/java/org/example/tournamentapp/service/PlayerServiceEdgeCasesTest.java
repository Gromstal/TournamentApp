package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.exception.PlayerNotFoundException;
import org.example.tournamentapp.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceEdgeCasesTest {

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
    void saveBothOpponentsTest() {
        service.saveOpponents(1L, 2L);

        verify(playerRepository, times(1)).saveOpponents(1L, 2L);
        verify(playerRepository, times(1)).saveOpponents(2L, 1L);
    }

    @Test
    void playerFoundTest() {
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        PlayerEntity result = service.getPlayerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void playerNotFoundTest() {
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> service.getPlayerById(999L));
    }

    @Test
    void playersMapTest() {
        PlayerEntity p1 = new PlayerEntity();
        p1.setId(1L);
        PlayerEntity p2 = new PlayerEntity();
        p2.setId(2L);

        when(playerRepository.findAllByTournamentId(100L)).thenReturn(List.of(p1, p2));

        Map<Long, PlayerEntity> result = service.getPlayers(100L);

        assertThat(result).hasSize(2);
        assertThat(result).containsKeys(1L, 2L);
    }

    @Test
    void playersEmptyTest() {
        when(playerRepository.findAllByTournamentId(100L)).thenReturn(List.of());

        Map<Long, PlayerEntity> result = service.getPlayers(100L);

        assertThat(result).isEmpty();
    }

    @Test
    void entityProxyTest() {
        PlayerEntity player = new PlayerEntity();
        player.setId(5L);

        when(playerRepository.getReferenceById(5L)).thenReturn(player);

        PlayerEntity result = service.getEntityReference(5L);

        assertNotNull(result);
        verify(playerRepository, times(1)).getReferenceById(5L);
    }

    @Test
    void opponentsSameIdTest() {
        service.saveOpponents(1L, 1L);

        verify(playerRepository, times(2)).saveOpponents(1L, 1L);
    }

    @Test
    void playersMultipleTest() {
        PlayerEntity p1 = new PlayerEntity();
        p1.setId(1L);
        PlayerEntity p2 = new PlayerEntity();
        p2.setId(2L);
        PlayerEntity p3 = new PlayerEntity();
        p3.setId(3L);

        when(playerRepository.findAllByTournamentId(100L)).thenReturn(List.of(p1, p2, p3));

        Map<Long, PlayerEntity> result = service.getPlayers(100L);

        assertThat(result).hasSize(3);
        assertThat(result).containsKeys(1L, 2L, 3L);
    }
}


