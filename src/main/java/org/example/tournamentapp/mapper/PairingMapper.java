package org.example.tournamentapp.mapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PairingEntity;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.model.PairDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Component
@RequiredArgsConstructor
public class PairingMapper {

    private final PlayerMapper playerMapper;

    public List<PairDto> toDtoList(Map<Long, Set<String>> opponentsMap,List<PairingEntity> pairingEntityList) {
        return pairingEntityList.stream()
                .map(pairingEntity -> {
                    return PairDto.builder()
                            .firstPlayer(playerMapper.toDto(opponentsMap.get(pairingEntity.getFirstPlayer().getId()),pairingEntity.getFirstPlayer()))
                            .secondPlayer(playerMapper.toDto(opponentsMap.get(pairingEntity.getSecondPlayer().getId()),pairingEntity.getSecondPlayer()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public PairingEntity toEntity(PairDto pair,
                                  PlayerEntity firstPlayer,
                                  PlayerEntity secondPlayer,
                                  int currentTour,
                                  Tournament tournament) {
        PairingEntity pairingEntity = new PairingEntity();
        pairingEntity.setFirstPlayer(firstPlayer);
        pairingEntity.setSecondPlayer(secondPlayer);
        pairingEntity.setTournament(tournament);
        pairingEntity.setCurrentTour(currentTour);
        return pairingEntity;
    }
}
