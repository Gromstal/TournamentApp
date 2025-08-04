package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final ProxyBotBuilder proxyBotBuilder;
    private final PlayerListWrapper playerListWrapper;
    private final TourService tourService;
    private final PlayerService playerService;
    private final TournamentService tournamentService;

    public List<Pair> createRandomPairList(List<Player> setupList) {
        Map<Long,PlayerEntity> players = getPlayers();
        List<Player> playerList = setupList
                .stream()
                .filter(player -> !player.getName().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(playerList);

        List<Pair> pairList = new ArrayList<>();

        for (int i = 0; i < playerList.size() - 1; i += 2) {
            pairList.add(new Pair(playerList.get(i), playerList.get(i + 1)));
            playerList.get(i).getNamesPlayed().add(playerList.get(i + 1).getName());
            playerList.get(i + 1).getNamesPlayed().add(playerList.get(i).getName());

            PlayerEntity player = players.get(playerList.get(i).getId());
            PlayerEntity opponent = players.get(playerList.get(i + 1).getId());
            playerService.saveOpponents(player, opponent);
        }

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
        Map<Long, PlayerEntity> players = getPlayers();
        List<Pair> pairList = new ArrayList<>();

        for (int i = 0; i < playerList.size() - 1; i++) {
            Player player1 = playerList.get(i);
            if (player1.isInPair()) continue;

            for (int j = i + 1; j < playerList.size(); j++) {
                Player player2 = playerList.get(j);
                if (player2.isInPair()) continue;

                if (!player1.getNamesPlayed().contains(player2.getName())) {
                    createPair(pairList, players, player1, player2);
                    break;
                }
            }
        }

        List<Player> unpaired = playerList.stream()
                .filter(p -> !p.isInPair())
                .toList();

        for (int i = 0; i < unpaired.size() - 1; i += 2) {
            Player player1 = unpaired.get(i);
            Player player2 = unpaired.get(i + 1);
            createPair(pairList, players, player1, player2);
        }

        for (Player player : playerList) {
            player.setInPair(false);
        }

        return pairList;
    }

    public PlayerListWrapper setupPlayerList() {
        playerListWrapper.setPlayerList(new ArrayList<>());
        return playerListWrapper;
    }

    public Player getProxyBot() {
        return proxyBotBuilder.getProxyBot();
    }

    public List<Player> getPlayerListWithPB(List<Player> setupList) {
        if (setupList.size() % 2 != 0) {
            setupList.add(getProxyBot());
        }
        return setupList;
    }

    private void createPair(List<Pair> pairList, Map<Long, PlayerEntity> players, Player p1, Player p2) {
        p1.setInPair(true);
        p2.setInPair(true);

        p1.getNamesPlayed().add(p2.getName());
        p2.getNamesPlayed().add(p1.getName());

        PlayerEntity entity1 = players.get(p1.getId());
        PlayerEntity entity2 = players.get(p2.getId());
        playerService.saveOpponents(entity1, entity2);

        pairList.add(new Pair(p1, p2));
    }

    private Map<Long,PlayerEntity> getPlayers(){
        Long tournamentId = tournamentService.getTournamentIdByTourDate(LocalDate.now());
        List<PlayerEntity> players = tournamentService.getAllPlayersByTournamentId(tournamentId);
        return players.stream().collect(Collectors.toMap(PlayerEntity::getId, p -> p));
    }
}
