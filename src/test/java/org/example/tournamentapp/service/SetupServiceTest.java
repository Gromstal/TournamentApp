package org.example.tournamentapp.service;
import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetupServiceTest {

    @Mock
    private ProxyBotBuilder proxyBotBuilder;

    @Mock
    private PlayerListWrapper playerListWrapper;

    @Mock
    private TourService tourService;

    @InjectMocks
    private SetupService setupService;


    @Test
    void testCreateRandomPairList() {
        List<Player> players = Arrays.asList(
                Player.builder().name("Alice").build(),
                Player.builder().name("Bob").build(),
                Player.builder().name("Charlie").build(),
                Player.builder().name("Diana").build()
        );

        List<Pair> pairs = setupService.createRandomPairList(players);

        assertEquals(2, pairs.size(), "Должно быть 2 пары");

        for (Pair pair : pairs) {
            assertNotNull(pair.getFirstPlayer());
            assertNotNull(pair.getSecondPlayer());
            assertNotEquals(pair.getFirstPlayer().getName(), pair.getSecondPlayer().getName());

            assertTrue(pair.getFirstPlayer().getNamesPlayed().contains(pair.getSecondPlayer().getName()),
                    "Игрок должен добавить второго в namesPlayed");
            assertTrue(pair.getSecondPlayer().getNamesPlayed().contains(pair.getFirstPlayer().getName()),
                    "Игрок должен добавить первого в namesPlayed");
        }
    }

    @Test
    void testCreateHandPairList() {
        List<Player> players = Arrays.asList(
                Player.builder().build(),
                Player.builder().build(),
                Player.builder().build()
        );

        List<Pair> pairs = setupService.createHandPairList(players);

        assertEquals(2, pairs.size(), "На 3 игроков должно быть 2 пары (с последним null или пустым)");

        for (Pair pair : pairs) {
            assertNotNull(pair.getFirstPlayer());
            assertNotNull(pair.getSecondPlayer());
        }
    }

    @Test
    void testCreateTourPairList_NoRepeatedPairs() {
        Player p1 = Player.builder().name("Alice").build();
        Player p2 = Player.builder().name("Bob").build();
        Player p3 = Player.builder().name("Charlie").build();
        Player p4 = Player.builder().name("Diana").build();

        p1.getNamesPlayed().add("Bob");
        p2.getNamesPlayed().add("Alice");

        List<Player> players = Arrays.asList(p1, p2, p3, p4);

        when(tourService.getSortedPlayerList(players)).thenReturn(players);

        List<Pair> pairs = setupService.createTourPairList(players);

        assertEquals(2, pairs.size(), "Должно быть 2 пары");


        for (Pair pair : pairs) {
            String first = pair.getFirstPlayer().getName();
            String second = pair.getSecondPlayer().getName();

            assertFalse(
                    (first.equals("Alice") && second.equals("Bob")) ||
                            (first.equals("Bob") && second.equals("Alice")),
                    "Alice и Bob не должны быть в паре снова"
            );
        }


        for (Player player : players) {
            assertFalse(player.isInPair(), player.getName() + " должен быть свободен");
        }

        for (Pair pair : pairs) {
            assertTrue(pair.getFirstPlayer().getNamesPlayed().contains(pair.getSecondPlayer().getName()));
            assertTrue(pair.getSecondPlayer().getNamesPlayed().contains(pair.getFirstPlayer().getName()));
        }
    }

    @Test
    void testExtractPlayersFromPairs() {
        Player p1 = Player.builder().name("Alice").build();
        Player p2 = Player.builder().name("Bob").build();
        Player p3 = Player.builder().name("Charlie").build();
        Player p4 = Player.builder().name("Diana").build();

        List<Pair> pairs = Arrays.asList(
                new Pair(p1, p2),
                new Pair(p3, p4)
        );

        List<Player> result = setupService.extractPlayersFromPairs(pairs);

        assertEquals(4, result.size());
        assertTrue(result.containsAll(Arrays.asList(p1, p2, p3, p4)));

        for (Pair pair : pairs) {
            assertTrue(pair.getFirstPlayer().getNamesPlayed().contains(pair.getSecondPlayer().getName()));
            assertTrue(pair.getSecondPlayer().getNamesPlayed().contains(pair.getFirstPlayer().getName()));
        }
    }

    @Test
    void testSetupPlayerList() {
        setupService.setupPlayerList();
        verify(playerListWrapper).setPlayerList(argThat(list -> list != null && list.isEmpty()));
    }

    @Test
    void testGetProxyBot() {
        Player proxyBot = Player.builder().name("Proxy Bot").build();
        when(proxyBotBuilder.getProxyBot()).thenReturn(proxyBot);

        Player result = setupService.getProxyBot();

        assertEquals(proxyBot.getName(), result.getName());
    }
}
