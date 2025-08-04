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
        Map<Long,PlayerEntity> players = getPlayers();
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

                    PlayerEntity player = players.get(playerList.get(i).getId());
                    PlayerEntity opponent = players.get(playerList.get(j).getId());
                    playerService.saveOpponents(player, opponent);

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

    private Map<Long,PlayerEntity> getPlayers(){
        Long tournamentId = tournamentService.getTournamentIdByTourDate(LocalDate.now());
        List<PlayerEntity> players = tournamentService.getAllPlayersByTournamentId(tournamentId);
        return players.stream().collect(Collectors.toMap(PlayerEntity::getId, p -> p));
    }
}
