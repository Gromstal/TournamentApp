package org.example.tournamentapp.service;

import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.testData.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFallbackPairingServiceTest {

    @Mock
    private SortingPlayerService sortingPlayerService;
    @InjectMocks
    private CreateFallbackPairingService service;
    private TestData testData;

    @BeforeEach
    void setUp() {
        testData = new TestData();
    }

    @Test
    void fallbackNoHistoryTest() {
        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        when(sortingPlayerService.getSortedPlayerList(players)).thenReturn(players);

        List<PairDto> result = service.getFastFallbackPairing(players);

        assertEquals(players.size() / 2, result.size());
        assertAllPlayersUsedOnce(players, result);
    }

    @Test
    void fallbackWithHistoryTest() {
        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();

        players.get(0).getNamesPlayed().add(players.get(1).getName());
        players.get(1).getNamesPlayed().add(players.get(0).getName());

        when(sortingPlayerService.getSortedPlayerList(players)).thenReturn(players);

        List<PairDto> result = service.getFastFallbackPairing(players);

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    void fallbackTwoPlayersTest() {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().id(1L).name("P1").namesPlayed(new HashSet<>()).build(),
                PlayerDto.builder().id(2L).name("P2").namesPlayed(new HashSet<>()).build()
        );

        when(sortingPlayerService.getSortedPlayerList(players)).thenReturn(players);

        List<PairDto> result = service.getFastFallbackPairing(players);

        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).getFirstPlayer().getName());
        assertEquals("P2", result.get(0).getSecondPlayer().getName());
    }

    @Test
    void fallbackEmptyTest() {
        when(sortingPlayerService.getSortedPlayerList(List.of())).thenReturn(List.of());

        List<PairDto> result = service.getFastFallbackPairing(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void fallbackSkipsOpponentTest() {
        PlayerDto p1 = PlayerDto.builder().id(1L).name("P1").namesPlayed(new HashSet<>()).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("P2").namesPlayed(new HashSet<>()).build();
        PlayerDto p3 = PlayerDto.builder().id(3L).name("P3").namesPlayed(new HashSet<>()).build();
        PlayerDto p4 = PlayerDto.builder().id(4L).name("P4").namesPlayed(new HashSet<>()).build();

        p1.getNamesPlayed().add("P2");
        p2.getNamesPlayed().add("P1");

        List<PlayerDto> players = List.of(p1, p2, p3, p4);
        when(sortingPlayerService.getSortedPlayerList(players)).thenReturn(players);

        List<PairDto> result = service.getFastFallbackPairing(players);

        assertEquals(2, result.size());
        boolean p1PairedWithP2 = result.stream()
                .anyMatch(pair -> 
                    (pair.getFirstPlayer().getName().equals("P1") && pair.getSecondPlayer().getName().equals("P2")) ||
                    (pair.getFirstPlayer().getName().equals("P2") && pair.getSecondPlayer().getName().equals("P1"))
                );
        assertFalse(p1PairedWithP2, "P1 should not be paired with P2 if possible");
    }

    @Test
    void fallbackAllPlayedTest() {
        PlayerDto p1 = PlayerDto.builder().id(1L).name("P1").namesPlayed(new HashSet<>(Set.of("P2"))).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("P2").namesPlayed(new HashSet<>(Set.of("P1"))).build();

        List<PlayerDto> players = List.of(p1, p2);
        when(sortingPlayerService.getSortedPlayerList(players)).thenReturn(players);

        List<PairDto> result = service.getFastFallbackPairing(players);

        assertNotNull(result);
    }

    private void assertAllPlayersUsedOnce(List<PlayerDto> allPlayers, List<PairDto> pairs) {
        Set<String> usedPlayers = new HashSet<>();
        for (PairDto pair : pairs) {
            assertTrue(usedPlayers.add(pair.getFirstPlayer().getName()), 
                    "Player used more than once: " + pair.getFirstPlayer().getName());
            assertTrue(usedPlayers.add(pair.getSecondPlayer().getName()), 
                    "Player used more than once: " + pair.getSecondPlayer().getName());
        }
        
        for (PlayerDto player : allPlayers) {
            if (!usedPlayers.contains(player.getName())) {
                fail("Player not used: " + player.getName());
            }
        }
    }
}

