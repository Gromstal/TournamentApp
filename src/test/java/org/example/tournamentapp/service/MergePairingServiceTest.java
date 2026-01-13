package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PairingEntity;
import org.example.tournamentapp.mapper.PairingMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MergePairingServiceTest {

    @Mock
    private PairingRepository pairingRepository;
    @Mock
    private PairingMapper pairingMapper;
    @InjectMocks
    private MergePairingService service;

    @Test
    void pairingListTest() {
        Map<Long, Set<String>> opponentsMap = new HashMap<>();
        List<PairingEntity> entities = new ArrayList<>();
        List<PairDto> expected = new ArrayList<>();

        when(pairingRepository.findByTournament_IdAndCurrentTour(1L, 1)).thenReturn(entities);
        when(pairingMapper.toDtoList(opponentsMap, entities)).thenReturn(expected);

        List<PairDto> result = service.getPairingList(opponentsMap, 1L, 1);

        assertNotNull(result);
        verify(pairingRepository, times(1)).findByTournament_IdAndCurrentTour(1L, 1);
        verify(pairingMapper, times(1)).toDtoList(opponentsMap, entities);
    }

    @Test
    void pairingEmptyTest() {
        when(pairingRepository.findByTournament_IdAndCurrentTour(1L, 1))
                .thenReturn(List.of());
        when(pairingMapper.toDtoList(anyMap(), anyList())).thenReturn(List.of());

        List<PairDto> result = service.getPairingList(Map.of(), 1L, 1);

        assertThat(result).isEmpty();
    }

    @Test
    void pairingMappedTest() {
        PairingEntity entity = new PairingEntity();
        when(pairingRepository.findByTournament_IdAndCurrentTour(1L, 2))
                .thenReturn(List.of(entity));
        when(pairingMapper.toDtoList(anyMap(), anyList()))
                .thenReturn(List.of(new PairDto()));

        List<PairDto> result = service.getPairingList(Map.of(), 1L, 2);

        assertThat(result).hasSize(1);
        verify(pairingMapper, times(1)).toDtoList(anyMap(), anyList());
    }

    @Test
    void mergeScoresTest() {
        PlayerDto sessionP1 = createPlayer("P1");
        sessionP1.setMp(0);
        sessionP1.setAp(0);

        PlayerDto sessionP2 = createPlayer("P2");
        sessionP2.setMp(0);
        sessionP2.setAp(0);

        PlayerDto formP1 = createPlayer("P1");
        formP1.setMp(10);
        formP1.setAp(5);

        PlayerDto formP2 = createPlayer("P2");
        formP2.setMp(8);
        formP2.setAp(7);

        List<PairDto> sessionPairs = List.of(new PairDto(sessionP1, sessionP2));
        PairsWrapper wrapper = new PairsWrapper(List.of(new PairDto(formP1, formP2)));

        service.mergePairs(sessionPairs, wrapper);

        assertEquals(10, sessionP1.getMp());
        assertEquals(5, sessionP1.getAp());
        assertEquals(8, sessionP2.getMp());
        assertEquals(7, sessionP2.getAp());
    }

    @Test
    void mergeMultipleTest() {
        List<PairDto> sessionPairs = new ArrayList<>();
        List<PairDto> formPairs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            PlayerDto sp1 = createPlayer("P" + (i * 2));
            sp1.setMp(0);
            sp1.setAp(0);
            PlayerDto sp2 = createPlayer("P" + (i * 2 + 1));
            sp2.setMp(0);
            sp2.setAp(0);

            PlayerDto fp1 = createPlayer("P" + (i * 2));
            fp1.setMp(i + 10);
            fp1.setAp(i + 5);
            PlayerDto fp2 = createPlayer("P" + (i * 2 + 1));
            fp2.setMp(i + 8);
            fp2.setAp(i + 6);

            sessionPairs.add(new PairDto(sp1, sp2));
            formPairs.add(new PairDto(fp1, fp2));
        }

        PairsWrapper wrapper = new PairsWrapper(formPairs);
        service.mergePairs(sessionPairs, wrapper);

        assertEquals(10, sessionPairs.get(0).getFirstPlayer().getMp());
        assertEquals(11, sessionPairs.get(1).getFirstPlayer().getMp());
        assertEquals(12, sessionPairs.get(2).getFirstPlayer().getMp());
    }

    @Test
    void pairingOpponentsTest() {
        Map<Long, Set<String>> opponentsMap = Map.of(
                1L, Set.of("Player2"),
                2L, Set.of("Player1")
        );

        when(pairingRepository.findByTournament_IdAndCurrentTour(1L, 1))
                .thenReturn(List.of());
        when(pairingMapper.toDtoList(opponentsMap, List.of())).thenReturn(List.of());

        List<PairDto> result = service.getPairingList(opponentsMap, 1L, 1);

        verify(pairingMapper, times(1)).toDtoList(opponentsMap, List.of());
    }

    private PlayerDto createPlayer(String name) {
        return PlayerDto.builder()
                .name(name)
                .faction("F")
                .build();
    }
}


