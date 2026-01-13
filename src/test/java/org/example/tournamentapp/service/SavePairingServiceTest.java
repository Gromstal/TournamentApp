package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PairingEntity;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.example.tournamentapp.mapper.PairingMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavePairingServiceTest {

    @Mock
    private PlayerService playerService;
    @Mock
    private PairingRepository pairingRepository;
    @Mock
    private PairingMapper pairingMapper;
    @Mock
    private TournamentRepository tournamentRepository;
    @InjectMocks
    private SavePairingService service;

    @Test
    void savePairingTest() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setCurrentTour(2);

        PlayerDto p1 = PlayerDto.builder().id(1L).name("P1").build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("P2").build();
        PairDto pair = new PairDto(p1, p2);

        PlayerEntity e1 = new PlayerEntity();
        PlayerEntity e2 = new PlayerEntity();
        PairingEntity entity = new PairingEntity();

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getEntityReference(1L)).thenReturn(e1);
        when(playerService.getEntityReference(2L)).thenReturn(e2);
        when(pairingMapper.toEntity(pair, e1, e2, 2, tournament)).thenReturn(entity);

        service.savePairingList(List.of(pair), 1L);

        verify(pairingRepository, times(1)).saveAll(anyList());
    }

    @Test
    void pairingTournamentNotFoundTest() {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class,
                () -> service.savePairingList(List.of(), 999L));
    }

    @Test
    void opponentsManualTest() {
        PlayerDto p1 = PlayerDto.builder().id(1L).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).build();
        PlayerDto p3 = PlayerDto.builder().id(3L).build();
        PlayerDto p4 = PlayerDto.builder().id(4L).build();

        List<PairDto> pairs = List.of(
                new PairDto(p1, p2),
                new PairDto(p3, p4)
        );

        service.saveOpponentsManualSetup(pairs, 1L);

        verify(playerService, times(1)).saveOpponents(1L, 2L);
        verify(playerService, times(1)).saveOpponents(3L, 4L);
    }

    @Test
    void pairingMultipleTest() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setCurrentTour(1);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(playerService.getEntityReference(anyLong())).thenReturn(new PlayerEntity());
        when(pairingMapper.toEntity(any(), any(), any(), anyInt(), any())).thenReturn(new PairingEntity());

        List<PairDto> pairs = List.of(
                new PairDto(PlayerDto.builder().id(1L).build(), PlayerDto.builder().id(2L).build()),
                new PairDto(PlayerDto.builder().id(3L).build(), PlayerDto.builder().id(4L).build()),
                new PairDto(PlayerDto.builder().id(5L).build(), PlayerDto.builder().id(6L).build())
        );

        service.savePairingList(pairs, 1L);

        verify(pairingRepository, times(1)).saveAll(argThat(list -> 
            list != null && ((java.util.List<?>) list).size() == 3));
    }

    @Test
    void pairingEmptyTest() {
        Tournament tournament = new Tournament();
        tournament.setCurrentTour(1);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        service.savePairingList(new ArrayList<>(), 1L);

        verify(pairingRepository, times(1)).saveAll(anyList());
    }

    @Test
    void pairingSingleTest() {
        Tournament tournament = new Tournament();
        tournament.setCurrentTour(1);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        PlayerDto p1 = PlayerDto.builder().id(1L).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).build();
        PairDto pair = new PairDto(p1, p2);

        PlayerEntity e1 = new PlayerEntity();
        PlayerEntity e2 = new PlayerEntity();
        when(playerService.getEntityReference(1L)).thenReturn(e1);
        when(playerService.getEntityReference(2L)).thenReturn(e2);

        PairingEntity entity = new PairingEntity();
        when(pairingMapper.toEntity(any(PairDto.class), any(), any(), anyInt(), any())).thenReturn(entity);

        service.savePairingList(List.of(pair), 1L);

        verify(pairingRepository, times(1)).saveAll(anyList());
    }

    @Test
    void opponentsEmptyTest() {
        service.saveOpponentsManualSetup(new ArrayList<>(), 1L);

        verify(playerService, never()).saveOpponents(anyLong(), anyLong());
    }

    @Test
    void opponentsSingleTest() {
        PlayerDto p1 = PlayerDto.builder().id(1L).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).build();
        PairDto pair = new PairDto(p1, p2);

        service.saveOpponentsManualSetup(List.of(pair), 1L);

        verify(playerService, times(1)).saveOpponents(1L, 2L);
    }


    @Test
    void opponentsMultipleTest() {
        PlayerDto p1 = PlayerDto.builder().id(1L).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).build();
        PlayerDto p3 = PlayerDto.builder().id(3L).build();
        PlayerDto p4 = PlayerDto.builder().id(4L).build();

        service.saveOpponentsManualSetup(List.of(
                new PairDto(p1, p2),
                new PairDto(p3, p4)
        ), 1L);

        verify(playerService, times(1)).saveOpponents(1L, 2L);
        verify(playerService, times(1)).saveOpponents(3L, 4L);
    }
}


