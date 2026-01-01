package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatingPairingService {

    private final SortingPlayerService sortingPlayerService;
    private final PlayerService playerService;

    public List<PairDto> createRandomPairList(List<PlayerDto> setupList) {
        Map<Long, PlayerEntity> players = playerService.getPlayers();
        List<PlayerDto> playerDtoList = setupList
                .stream()
                .filter(player -> !player.getName().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(playerDtoList);

        List<PairDto> pairList = new ArrayList<>();

        for (int i = 0; i < playerDtoList.size() - 1; i += 2) {
            pairList.add(new PairDto(playerDtoList.get(i), playerDtoList.get(i + 1)));
            playerDtoList.get(i).getNamesPlayed().add(playerDtoList.get(i + 1).getName());
            playerDtoList.get(i + 1).getNamesPlayed().add(playerDtoList.get(i).getName());

            PlayerEntity player = players.get(playerDtoList.get(i).getId());
            PlayerEntity opponent = players.get(playerDtoList.get(i + 1).getId());
            playerService.saveOpponents(player, opponent);
        }

        return pairList;
    }

    public List<PairDto> createManualPairList(List<PlayerDto> setupList) {
        int pairCount = setupList.size() / 2 + setupList.size() % 2;
        List<PairDto> pairs = new ArrayList<>();

        for (int i = 0; i < pairCount; i++) {
            pairs.add(new PairDto(new PlayerDto(), new PlayerDto()));
        }
        return pairs;
    }

    public List<PairDto> createTourPairList(List<PlayerDto> setupList) {
        List<PlayerDto> players = sortingPlayerService.getSortedPlayerList(setupList);
        Map<Long, PlayerEntity> playersById = playerService.getPlayers();

        PairSearchResult best = findBestPairs(players);

        if (!best.pairs.isEmpty()) {
            finalizePairs(best.pairs, playersById);
        }
        return best.pairs;
    }

    private PairSearchResult findBestPairs(List<PlayerDto> players) {
        /*
           Алгоритм Backtracking with Branch and Bound
           Формируем допустимые наборы пар (без повторяющихся пар),
           накапливая их общую стоимость
           (стоимость = сумма расстояний между индексами игроков в списке).

           После получения полного набора пар сравниваем его стоимость
           с лучшей найденной, сохраняем более дешевый вариант и
           откатываемся назад, чтобы перебрать альтернативные разбиения
           и найти минимальную возможную стоимость.
        */

        boolean[] used = new boolean[players.size()];
        List<PairDto> current = new ArrayList<>(players.size() / 2);

        PairSearchResult best = new PairSearchResult(Integer.MAX_VALUE, new ArrayList<>());

        backtrack(players, used, 0, current, best);
        return best;
    }

    private void backtrack(List<PlayerDto> players,
                           boolean[] used,
                           int currentCost,
                           List<PairDto> currentPairs,
                           PairSearchResult best) {

        if (currentCost >= best.cost) return;

        int i = selectNextPlayerIndex(used);
        if (i == -1) {
            best.cost = currentCost;
            best.pairs = new ArrayList<>(currentPairs);
            return;
        }

        used[i] = true;
        PlayerDto p1 = players.get(i);

        for (int j = i + 1; j < players.size(); j++) {
            if (used[j]) continue;

            PlayerDto p2 = players.get(j);
            if (isPlayed(p1, p2)) continue;

            used[j] = true;
            currentPairs.add(new PairDto(p1, p2));
            int addCost = j - i;

            backtrack(players, used, currentCost + addCost, currentPairs, best);
            currentPairs.remove(currentPairs.size() - 1);
            used[j] = false;
        }

        used[i] = false;
    }

    private int selectNextPlayerIndex(boolean[] used) {
        for (int i = 0; i < used.length; i++) {
            if (!used[i]) return i;
        }
        return -1;
    }

    private boolean isPlayed(PlayerDto a, PlayerDto b) {
        return a.getNamesPlayed().contains(b.getName()) || b.getNamesPlayed().contains(a.getName());
    }

    private void finalizePairs(List<PairDto> pairList, Map<Long, PlayerEntity> players) {
        for (PairDto pair : pairList) {
            PlayerDto p1 = pair.getFirstPlayer();
            PlayerDto p2 = pair.getSecondPlayer();

            p1.getNamesPlayed().add(p2.getName());
            p2.getNamesPlayed().add(p1.getName());

            PlayerEntity entity1 = players.get(p1.getId());
            PlayerEntity entity2 = players.get(p2.getId());
            playerService.saveOpponents(entity1, entity2);
        }
    }

    private static final class PairSearchResult {
        int cost;
        List<PairDto> pairs;

        PairSearchResult(int cost, List<PairDto> pairs) {
            this.cost = cost;
            this.pairs = pairs;
        }
    }

}
