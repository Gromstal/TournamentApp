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
    @Mock
    CreateFallbackPairingService createFallbackPairingService;
    @InjectMocks
    CreatingPairingService service;

    private TestData testData;
    private final Long tournamentId = 1L;

    @BeforeEach
    void setUp() {
        testData = new TestData();
        setPairingTimeout(service, "pairingTimeoutMs", 5000L);
    }

    private void setPairingTimeout(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
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

    @Test
    void timeoutAndFallbackTest() {
        setPairingTimeout(service, "pairingTimeoutMs", 0L);

        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        Map<Long, PlayerEntity> entityMap = testData.toEntityMap(players);

        List<PairDto> fallbackPairs = new ArrayList<>();
        for (int i = 0; i < players.size() - 1; i += 2) {
            fallbackPairs.add(new PairDto(players.get(i), players.get(i + 1)));
        }

        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(players);
        when(playerService.getPlayers(tournamentId)).thenReturn(entityMap);
        when(createFallbackPairingService.getFastFallbackPairing(anyList())).thenReturn(fallbackPairs);

        List<PairDto> result = service.createTourPairList(tournamentId, players);

        assertNotNull(result, "Result should not be null");
        assertEquals(fallbackPairs.size(), result.size(), "Should return fallback pairs");
        verify(createFallbackPairingService, times(1)).getFastFallbackPairing(anyList());
        verify(playerService, times(result.size())).saveOpponents(anyLong(), anyLong());
    }

    @Test
    void fallbackPairsFinalizedTest() {
        setPairingTimeout(service, "pairingTimeoutMs", 0L);

        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        Map<Long, PlayerEntity> entityMap = testData.toEntityMap(players);

        List<PairDto> fallbackPairs = new ArrayList<>();
        PlayerDto p1 = players.get(0);
        PlayerDto p2 = players.get(1);
        fallbackPairs.add(new PairDto(p1, p2));

        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(players);
        when(playerService.getPlayers(tournamentId)).thenReturn(entityMap);
        when(createFallbackPairingService.getFastFallbackPairing(anyList())).thenReturn(fallbackPairs);

        List<PairDto> result = service.createTourPairList(tournamentId, players);

        assertEquals(1, result.size());
        verify(playerService, times(1)).saveOpponents(p1.getId(), p2.getId());

        assertTrue(p1.getNamesPlayed().contains(p2.getName()), 
                   "У 1 игрока должен быть в списке оппонентов игрок 2");
        assertTrue(p2.getNamesPlayed().contains(p1.getName()), 
                   "У 2 игрока должен быть в списке оппонентов игрок 1");
    }

    @Test
    void emptyFallbackResultTest() {
        setPairingTimeout(service, "pairingTimeoutMs", 0L);

        List<PlayerDto> players = testData.getFinalResultListWithIdsAndEmptyHistory();
        Map<Long, PlayerEntity> entityMap = testData.toEntityMap(players);

        when(sortingPlayerService.getSortedPlayerList(anyList())).thenReturn(players);
        when(playerService.getPlayers(tournamentId)).thenReturn(entityMap);
        when(createFallbackPairingService.getFastFallbackPairing(anyList()))
                .thenReturn(new ArrayList<>());

        List<PairDto> result = service.createTourPairList(tournamentId, players);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Empty fallback result should be returned safely");
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
