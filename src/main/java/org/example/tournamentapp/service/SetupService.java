package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final ProxyBotBuilder proxyBotBuilder;
    private final PlayerListWrapper playerListWrapper;
    private final TourService tourService;

    public List<Pair> createRandomPairList(List<Player> setupList) {
        List<Player> playerList = setupList
                .stream()
                .filter(player -> !player.getName().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(playerList);

        List<Pair> pairList = new ArrayList<>();

        for (int i = 0; i < playerList.size() - 1; i += 2) {
            pairList.add(new Pair(playerList.get(i), playerList.get(i + 1)));
            playerList.get(i).getNamesPlayed().add(playerList.get(i + 1).getName());
            playerList.get(i+1).getNamesPlayed().add(playerList.get(i).getName());
        }
        System.out.println(pairList);

        return pairList;
    }

    public List<Pair> createHandPairList(List<Player> setupList) {
        int pairCount = setupList.size() / 2 + setupList.size() % 2;
        List<Pair> pairs = new ArrayList<>();

        for (int i = 0; i < pairCount; i++) {
            pairs.add(new Pair(new Player(), new Player()));
        }
        return pairs;
    }

    public List<Pair> createTourPairList(List<Player> setupList) {
        List<Player> playerList = tourService.getSortedPlayerList(setupList);

        List<Pair> pairList = new ArrayList<>();

        for (int i = 0; i < playerList.size() - 1; i++) {
            if (playerList.get(i).isInPair()) continue;

            for (int j = i + 1; j < playerList.size(); j++) {

                if (playerList.get(j).isInPair()) continue;

                if (!playerList.get(i).getNamesPlayed().contains(playerList.get(j).getName())) {

                    playerList.get(i).setInPair(true);
                    playerList.get(j).setInPair(true);

                    playerList.get(i).getNamesPlayed().add(playerList.get(j).getName());
                    playerList.get(j).getNamesPlayed().add(playerList.get(i).getName());

                    pairList.add(new Pair(playerList.get(i), playerList.get(j)));
                    break;
                }
            }
        }

        for (Player player : playerList) {
            player.setInPair(false);
        }
        return pairList;
    }

    public List<Player> extractPlayersFromPairs(List<Pair> pairs) {
        List<Player> players = new ArrayList<>();
        for (Pair pair : pairs) {
            if (pair.getFirstPlayer() != null) players.add(pair.getFirstPlayer());
            if (pair.getSecondPlayer() != null) players.add(pair.getSecondPlayer());
            pair.getFirstPlayer().getNamesPlayed().add(pair.getSecondPlayer().getName());
            pair.getSecondPlayer().getNamesPlayed().add(pair.getFirstPlayer().getName());
        }
        return players;
    }

    public PlayerListWrapper setupPlayerList() {
        playerListWrapper.setPlayerList(new ArrayList<>());
        return playerListWrapper;
    }
    public Player getProxyBot() {
        return proxyBotBuilder.getProxyBot();
    }
}
