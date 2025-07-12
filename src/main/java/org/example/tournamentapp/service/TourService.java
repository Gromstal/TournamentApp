package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TourService {

    private final CalculateService calculateService;

    public void calculateTourPoints(PairsWrapper pairsWrapper) {
        for (Pair pair : pairsWrapper.getPairs()) {
            calculateService.calculate(pair.getFirstPlayer(), pair.getSecondPlayer());
        }

    }

    public void mergePairs(List<Pair> sessionPairs, PairsWrapper pairsWrapper) {
        IntStream.range(0, sessionPairs.size()).forEach(i -> {
            Pair sessionPair = sessionPairs.get(i);
            Pair formPair = pairsWrapper.getPairs().get(i);

            sessionPair.getFirstPlayer().setMp(formPair.getFirstPlayer().getMp());
            sessionPair.getFirstPlayer().setAp(formPair.getFirstPlayer().getAp());
            sessionPair.getSecondPlayer().setMp(formPair.getSecondPlayer().getMp());
            sessionPair.getSecondPlayer().setAp(formPair.getSecondPlayer().getAp());
        });
    }

    public List<Player> getFinalSortedList(List<Player> setupList) {
        return  getSortedPlayerList(setupList);
    }

    public List<Player> getSortedPlayerList(List<Player> setupList) {
        List<Player> list= setupList
                .stream()
                .filter(player -> !player.getName().isEmpty())
                .sorted(Comparator.comparingInt(Player::getTp)
                        .thenComparing(Player::getVp)
                        .thenComparing(Player::getTotalMp)
                        .thenComparing(Player::getTotalAp))
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(list);

        return list;
    }
}
