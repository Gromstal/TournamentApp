package org.example.tournamentapp.mapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PairingEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.repository.PlayerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
@RequiredArgsConstructor
public class PairingMapper {

    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;

    public List<PairDto> toDtoList(List<PairingEntity> pairingEntityList) {
        return pairingEntityList.stream()
                .map(pairingEntityEntity -> {
                    return PairDto.builder()
                            .firstPlayer(playerMapper.toDto(pairingEntityEntity.getFirstPlayer()))
                            .secondPlayer(playerMapper.toDto(pairingEntityEntity.getSecondPlayer()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public PairingEntity toEntity(PairDto pair, int currentTour, Tournament tournament) {
        PairingEntity pairingEntity = new PairingEntity();
        pairingEntity.setFirstPlayer(playerMapper.getEntity(pair.getFirstPlayer().getId()));
        pairingEntity.setSecondPlayer(playerMapper.getEntity(pair.getSecondPlayer().getId()));
        pairingEntity.setTournament(tournament);
        pairingEntity.setCurrentTour(currentTour);
        return pairingEntity;
    }

    public List<PairingEntity> toEntityList(List<PairDto> pairList, int currentTour, Tournament tournament) {
        return pairList.stream()
                .map(pair -> toEntity(pair, currentTour, tournament))
                .collect(Collectors.toList());
    }
}
