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
        List<PlayerDto> playersSorted = sortingPlayerService.getSortedPlayerList(setupList);
        Map<Long, PlayerEntity> playersById = playerService.getPlayers();

        resetPairs(playersSorted);

        List<PairDto> bestPairs = new ArrayList<>();
        int[] bestCost = {Integer.MAX_VALUE};

        buildPairs(playersSorted, new ArrayList<>(), 0, bestPairs, bestCost);

        resetPairs(playersSorted);

        if (!bestPairs.isEmpty()) {
            finalizePairs(bestPairs, playersById);
        }
        return bestPairs;
    }

    private boolean buildPairs(List<PlayerDto> players,
                               List<PairDto> currentPairs,
                               int currentCost,
                               List<PairDto> bestPairs,
                               int[] bestCost) {

        if (currentCost >= bestCost[0]) return false;

        int firstFreeIndex = findFirstFreeIndex(players);
        if (firstFreeIndex == -1) {
            bestCost[0] = currentCost;
            bestPairs.clear();
            bestPairs.addAll(currentPairs);
            return true;
        }

        PlayerDto p1 = players.get(firstFreeIndex);
        p1.setInPair(true);

        List<Integer> candidates = findCandidates(players, firstFreeIndex, p1);
        if (candidates.isEmpty()) {
            p1.setInPair(false);
            return false;
        }

        boolean foundAny = false;

        for (int j : candidates) {
            PlayerDto p2 = players.get(j);
            p2.setInPair(true);

            currentPairs.add(new PairDto(p1, p2));
            int addCost = Math.abs(firstFreeIndex - j);

            boolean branchOk = buildPairs(players, currentPairs, currentCost + addCost, bestPairs, bestCost);
            foundAny |= branchOk;

            currentPairs.remove(currentPairs.size() - 1);
            p2.setInPair(false);

            if (bestCost[0] == (players.size() / 2)) {
                break;
            }
        }

        p1.setInPair(false);
        return foundAny;
    }

    private int findFirstFreeIndex(List<PlayerDto> players) {
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).isInPair()) return i;
        }
        return -1;
    }

    private List<Integer> findCandidates(List<PlayerDto> players, int fromIndex, PlayerDto p1) {
        List<Integer> candidateIndexes = new ArrayList<>(3);

        for (int j = fromIndex + 1; j < players.size(); j++) {
            PlayerDto p2 = players.get(j);

            if (p2.isInPair()) continue;
            if (p1.getNamesPlayed().contains(p2.getName())) continue;

            candidateIndexes.add(j);
            if (candidateIndexes.size() == 3) break;
        }
        return candidateIndexes;
    }

    private void resetPairs(List<PlayerDto> players) {
        for (PlayerDto p : players) {
            p.setInPair(false);
        }
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

}
