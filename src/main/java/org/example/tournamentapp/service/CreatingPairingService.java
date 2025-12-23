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
        List<PlayerDto> playerDtoList = sortingPlayerService.getSortedPlayerList(setupList);
        Map<Long, PlayerEntity> players = playerService.getPlayers();
        List<PairDto> pairList = new ArrayList<>();

        for (int i = 0; i < playerDtoList.size() - 1; i++) {
            PlayerDto playerDto1 = playerDtoList.get(i);
            if (playerDto1.isInPair()) continue;

            for (int j = i + 1; j < playerDtoList.size(); j++) {
                PlayerDto playerDto2 = playerDtoList.get(j);
                if (playerDto2.isInPair()) continue;

                if (!playerDto1.getNamesPlayed().contains(playerDto2.getName())) {
                    createPair(pairList, players, playerDto1, playerDto2);
                    break;
                }
            }
        }

        List<PlayerDto> unpaired = playerDtoList.stream()
                .filter(p -> !p.isInPair())
                .toList();

        for (int i = 0; i < unpaired.size() - 1; i += 2) {
            PlayerDto playerDto1 = unpaired.get(i);
            PlayerDto playerDto2 = unpaired.get(i + 1);
            createPair(pairList, players, playerDto1, playerDto2);
        }

        for (PlayerDto playerDto : playerDtoList) {
            playerDto.setInPair(false);
        }

        return pairList;
    }

    private void createPair(List<PairDto> pairList, Map<Long, PlayerEntity> players, PlayerDto p1, PlayerDto p2) {
        p1.setInPair(true);
        p2.setInPair(true);

        p1.getNamesPlayed().add(p2.getName());
        p2.getNamesPlayed().add(p1.getName());

        PlayerEntity entity1 = players.get(p1.getId());
        PlayerEntity entity2 = players.get(p2.getId());
        playerService.saveOpponents(entity1, entity2);

        pairList.add(new PairDto(p1, p2));
    }
}
