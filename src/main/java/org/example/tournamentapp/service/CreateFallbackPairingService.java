package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateFallbackPairingService {

    private final SortingPlayerService sortingPlayerService;

    public List<PairDto> getFastFallbackPairing(List<PlayerDto> setupList) {
        /*
           Если первоначальный алгоритм не уложился в таймаут, то
           используем более "жадный" выбор, но с минимальной проверкой
           есть ли у игроков потенциальные оппоненты.
        */

        List<PlayerDto> playerList = sortingPlayerService.getSortedPlayerList(setupList);

        int listSize = playerList.size();
        boolean[] played = new boolean[listSize];

        List<PairDto> pairs = new ArrayList<>(listSize / 2);

        for (int i = 0; i < listSize; i++) {
            if (played[i]) continue;

            PlayerDto p1 = playerList.get(i);

            for (int j = i + 1; j < listSize; j++) {
                if (played[j]) continue;

                PlayerDto p2 = playerList.get(j);

                if (isPlayed(p1, p2)) continue;

                if (hasOnlyOnePossibleOpponent(j, played, playerList)) {
                    continue;
                }

                pairs.add(new PairDto(p1, p2));
                played[i] = true;
                played[j] = true;
                break;
            }
        }

        List<Integer> unpairedIndexes = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            if (!played[i]) {
                unpairedIndexes.add(i);
            }
        }

        for (int k = 0; k < unpairedIndexes.size() - 1; k += 2) {
            PlayerDto p1 = playerList.get(unpairedIndexes.get(k));
            PlayerDto p2 = playerList.get(unpairedIndexes.get(k + 1));
            pairs.add(new PairDto(p1, p2));
        }

        return pairs;
    }

    private boolean hasOnlyOnePossibleOpponent(int index, boolean[] played, List<PlayerDto> players) {
        PlayerDto player = players.get(index);
        int count = 0;

        for (int i = 0; i < players.size(); i++) {
            if (i == index) continue;
            if (played[i]) continue;

            PlayerDto other = players.get(i);

            if (!isPlayed(player, other)) {
                count++;
                if (count > 1) return false;
            }
        }
        return count == 1;
    }

    private boolean isPlayed(PlayerDto a, PlayerDto b) {
        return a.getNamesPlayed().contains(b.getName()) || b.getNamesPlayed().contains(a.getName());
    }
}
