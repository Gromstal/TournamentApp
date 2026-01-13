package org.example.tournamentapp.service;

import org.example.tournamentapp.builder.TournamentBuilder;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.ManualSetupOption;
import org.example.tournamentapp.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentCreatingServiceTest {

    @Mock
    private TournamentBuilder tournamentBuilder;
    @Mock
    private PlayerMapper playerMapper;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private CreatingPairingService creatingPairingService;
    @Mock
    private SavePairingService savePairingService;
    @Mock
    private SetupService setupService;
    @InjectMocks
    private TournamentCreatingService service;

    @Test
    void createManualTest() {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("P1").build(),
                PlayerDto.builder().name("P2").build()
        );

        PlayerEntity e1 = new PlayerEntity();
        e1.setId(1L);
        PlayerEntity e2 = new PlayerEntity();
        e2.setId(2L);

        Tournament tournament = new Tournament();
        tournament.setId(100L);
        tournament.setPlayers(Set.of(e1, e2));

        when(setupService.setupPlayerListWithProxyBot(players)).thenReturn(players);
        when(playerMapper.getStartingEntityList(players)).thenReturn(List.of(e1, e2));
        when(tournamentBuilder.getNewTournament(any(), eq(5))).thenReturn(tournament);
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        ManualSetupOption result = service.create(players, 5, true);

        assertTrue(result.isManual());
        assertEquals(100L, result.tournamentId());
        verify(savePairingService, never()).savePairingList(any(), any());
    }

    @Test
    void createAutoTest() {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("P1").build(),
                PlayerDto.builder().name("P2").build()
        );

        PlayerEntity e1 = new PlayerEntity();
        e1.setId(1L);
        PlayerEntity e2 = new PlayerEntity();
        e2.setId(2L);

        Tournament tournament = new Tournament();
        tournament.setId(100L);
        tournament.setPlayers(Set.of(e1, e2));

        when(setupService.setupPlayerListWithProxyBot(players)).thenReturn(players);
        when(playerMapper.getStartingEntityList(players)).thenReturn(List.of(e1, e2));
        when(tournamentBuilder.getNewTournament(any(), eq(5))).thenReturn(tournament);
        when(tournamentRepository.save(tournament)).thenReturn(tournament);
        when(playerMapper.toDto(any(), any())).thenReturn(PlayerDto.builder().id(1L).build());
        when(creatingPairingService.createRandomPairList(anyLong(), anyList())).thenReturn(List.of(new PairDto()));

        ManualSetupOption result = service.create(players, 5, false);

        assertFalse(result.isManual());
        assertEquals(100L, result.tournamentId());
        verify(savePairingService, times(1)).savePairingList(any(), eq(100L));
    }

    @Test
    void createNullTest() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(null, 5, false));
    }

    @Test
    void createTooFewTest() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(List.of(PlayerDto.builder().build()), 5, false));
    }

    @Test
    void createZeroToursTest() {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("P1").build(),
                PlayerDto.builder().name("P2").build()
        );

        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setPlayers(new HashSet<>());

        when(setupService.setupPlayerListWithProxyBot(players)).thenReturn(players);
        when(playerMapper.getStartingEntityList(anyList())).thenReturn(List.of());
        when(tournamentBuilder.getNewTournament(any(), eq(0))).thenReturn(tournament);
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        ManualSetupOption result = service.create(players, 0, true);

        assertNotNull(result);
    }
}


