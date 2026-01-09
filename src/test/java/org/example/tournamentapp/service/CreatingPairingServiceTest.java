package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PlayerEntity;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatingPairingServiceTest {
    @Mock SortingPlayerService sortingPlayerService;
    @Mock
    PlayerService playerService;

    @InjectMocks
    CreatingPairingService service;

    private TestData testData;

    private final Long tournamentId = 1L;

    @BeforeEach
    void setUp() {
        testData = new TestData();
    }

    @Test
    void noRepeatsTest() {
        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        Map<Long, PlayerEntity> entityMap = testData.toEntityMap(players);

        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(players);
        when(playerService.getPlayers(tournamentId)).thenReturn(entityMap);

        Map<String, Set<String>> playedBefore = snapshotIsPlayed(players);

        List<PairDto> result = service.createTourPairList(tournamentId,players);

        assertEquals(players.size() / 2, result.size(), "Кол-во пар должно быть четным");

        assertEveryPlayerUsedExactlyOnce(players, result);
        assertNoDuplicatePairsInTour(result);
        assertNoPairWasIsPlayedBefore(result, playedBefore);

        verify(playerService, times(result.size())).saveOpponents(anyLong(), anyLong());
    }

    @Test
    void noRepeatsWithTourIsPlayedTest() {
        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        Map<Long, PlayerEntity> entityMap = testData.toEntityMap(players);

        forbidAdjacentPairs(players);

        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(players);
        when(playerService.getPlayers(tournamentId)).thenReturn(entityMap);

        Map<String, Set<String>> playedBefore = snapshotIsPlayed(players);

        List<PairDto> result = service.createTourPairList(tournamentId,players);

        assertEquals(players.size() / 2, result.size());
        assertEveryPlayerUsedExactlyOnce(players, result);
        assertNoDuplicatePairsInTour(result);
        assertNoPairWasIsPlayedBefore(result, playedBefore);

        verify(playerService, times(result.size())).saveOpponents(anyLong(), anyLong());
    }

    @Test
    void noRepeatsAcrossToursTest() {
        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        Map<Long, PlayerEntity> entityMap = testData.toEntityMap(players);

        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(players);
        when(playerService.getPlayers(tournamentId)).thenReturn(entityMap);

        Map<String, Set<String>> playedBefore1 = snapshotIsPlayed(players);
        List<PairDto> round1 = service.createTourPairList(tournamentId,players);
        assertEquals(players.size() / 2, round1.size());
        assertNoPairWasIsPlayedBefore(round1, playedBefore1);

        Map<String, Set<String>> playedBefore2 = snapshotIsPlayed(players);
        List<PairDto> round2 = service.createTourPairList(tournamentId,players);
        assertEquals(players.size() / 2, round2.size());
        assertNoPairWasIsPlayedBefore(round2, playedBefore2);

        Map<String, Set<String>> playedBefore3 = snapshotIsPlayed(players);
        List<PairDto> round3 = service.createTourPairList(tournamentId,players);
        assertEquals(players.size() / 2, round3.size());
        assertNoPairWasIsPlayedBefore(round3, playedBefore3);

        Set<String> allPairs = new HashSet<>();
        addPairsToSet(allPairs, round1);
        assertTrue(addPairsToSet(allPairs, round2), "Есть повтор пары между 1 и 2 туром");
        assertTrue(addPairsToSet(allPairs, round3), "Есть повтор пары между предыдущими турами и 3 туром");

        verify(playerService, times(3 * (players.size() / 2))).saveOpponents(anyLong(), anyLong());
    }

    @Test
    void noMatchingTest() {
        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        Map<Long, PlayerEntity> entityMap = testData.toEntityMap(players);

        PlayerDto p0 = players.get(0);
        for (int i = 1; i < players.size(); i++) {
            PlayerDto other = players.get(i);
            p0.getNamesPlayed().add(other.getName());
            other.getNamesPlayed().add(p0.getName());
        }

        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(players);
        when(playerService.getPlayers(tournamentId)).thenReturn(entityMap);

        List<PairDto> result = service.createTourPairList(tournamentId,players);

        assertTrue(result.isEmpty(), "Если матчинг невозможен — ожидаем пустой список пар");

        verify(playerService, never()).saveOpponents(anyLong(), anyLong());
    }


    private void assertEveryPlayerUsedExactlyOnce(List<PlayerDto> all, List<PairDto> pairs) {
        Map<String, Long> counts = pairs.stream()
                .flatMap(p -> Arrays.stream(new String[]{p.getFirstPlayer().getName(), p.getSecondPlayer().getName()}))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        assertEquals(all.size(), counts.size(), "В туре должны присутствовать все игроки");
        for (PlayerDto p : all) {
            assertEquals(1L, counts.getOrDefault(p.getName(), 0L), "Игрок должен встретиться строго 1 раз: " + p.getName());
        }
    }

    private void assertNoDuplicatePairsInTour(List<PairDto> pairs) {
        Set<String> seen = new HashSet<>();
        for (PairDto p : pairs) {
            String a = p.getFirstPlayer().getName();
            String b = p.getSecondPlayer().getName();
            String key = normalizePair(a, b);
            assertTrue(seen.add(key), "Дубль пары внутри тура: " + key);
        }
    }

    private void assertNoPairWasIsPlayedBefore(List<PairDto> pairs, Map<String, Set<String>> playedBefore) {
        for (PairDto p : pairs) {
            String a = p.getFirstPlayer().getName();
            String b = p.getSecondPlayer().getName();

            boolean wasPlayed = playedBefore.getOrDefault(a, Set.of()).contains(b)
                    || playedBefore.getOrDefault(b, Set.of()).contains(a);

            assertFalse(wasPlayed, "Пара уже играла: " + normalizePair(a, b));
        }
    }

    private Map<String, Set<String>> snapshotIsPlayed(List<PlayerDto> players) {
        Map<String, Set<String>> snap = new HashMap<>();
        for (PlayerDto p : players) {
            snap.put(p.getName(), new HashSet<>(p.getNamesPlayed()));
        }
        return snap;
    }

    private void forbidAdjacentPairs(List<PlayerDto> playersSorted) {
        for (int i = 0; i < playersSorted.size() - 1; i += 2) {
            PlayerDto a = playersSorted.get(i);
            PlayerDto b = playersSorted.get(i + 1);
            a.getNamesPlayed().add(b.getName());
            b.getNamesPlayed().add(a.getName());
        }
    }

    private String normalizePair(String a, String b) {
        return (a.compareTo(b) <= 0) ? (a + "||" + b) : (b + "||" + a);
    }

    private boolean addPairsToSet(Set<String> set, List<PairDto> pairs) {
        boolean ok = true;
        for (PairDto p : pairs) {
            String key = normalizePair(p.getFirstPlayer().getName(), p.getSecondPlayer().getName());
            if (!set.add(key)) ok = false;
        }
        return ok;
    }

}