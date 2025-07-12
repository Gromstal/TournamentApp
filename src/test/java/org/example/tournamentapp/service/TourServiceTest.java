package org.example.tournamentapp.service;

import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private CalculateService calculateService;

    @InjectMocks
    private TourService tourService;


    @Test
    void testCalculateTourPoints() {
        Player p1 = Player.builder().name("Alice").build();
        Player p2 = Player.builder().name("Bob").build();
        Pair pair = new Pair(p1, p2);

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(pair));

        tourService.calculateTourPoints(wrapper);

        verify(calculateService).calculate(p1, p2);
    }

    @Test
    void testMergePairs() {
        Player sessionP1 = Player.builder().name("Alice").build();
        Player sessionP2 = Player.builder().name("Bob").build();
        Pair sessionPair = new Pair(sessionP1, sessionP2);

        Player formP1 = Player.builder().name("Alice").ap(10).mp(20).build();
        Player formP2 = Player.builder().name("Bob").ap(30).mp(40).build();
        Pair formPair = new Pair(formP1, formP2);

        List<Pair> sessionPairs = new ArrayList<>(List.of(sessionPair));
        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(formPair));

        tourService.mergePairs(sessionPairs, wrapper);

        assertEquals(10, sessionP1.getAp());
        assertEquals(20, sessionP1.getMp());
        assertEquals(30, sessionP2.getAp());
        assertEquals(40, sessionP2.getMp());
    }

    @Test
    void testGetFinalSortedList() {
        Player p1 = Player.builder().name("Alice").tp(10).vp(50).totalMp(100).totalAp(200).build();
        Player p2 = Player.builder().name("Bob").tp(20).vp(30).totalMp(90).totalAp(150).build();
        Player p3 = Player.builder().name("").tp(30).build();

        List<Player> players = Arrays.asList(p1, p2, p3);

        List<Player> result = tourService.getFinalSortedList(players);


        assertEquals(2, result.size(), "Игрок с пустым именем должен быть исключен");
        assertEquals("Bob", result.get(0).getName(), "Игрок с наибольшим TP должен быть первым");
        assertEquals("Alice", result.get(1).getName());
    }

    @Test
    void testGetSortedPlayerList_Order() {
        Player p1 = Player.builder().name("A").tp(10).vp(50).totalMp(100).totalAp(200).build();
        Player p2 = Player.builder().name("B").tp(20).vp(30).totalMp(90).totalAp(150).build();
        Player p3 = Player.builder().name("C").tp(20).vp(30).totalMp(80).totalAp(140).build();

        List<Player> players = Arrays.asList(p1, p2, p3);

        List<Player> result = tourService.getSortedPlayerList(players);

        assertEquals("B", result.get(0).getName());
        assertEquals("C", result.get(1).getName());
        assertEquals("A", result.get(2).getName());
    }
}