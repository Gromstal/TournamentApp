package org.example.tournamentapp.mapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.Pairing;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.model.Pair;
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

    public List<Pair> toDtoList(List<Pairing> pairingList) {
        return pairingList.stream()
                .map(pairingEntity -> {
                    return Pair.builder()
                            .firstPlayer(playerMapper.toDto(pairingEntity.getFirstPlayer()))
                            .secondPlayer(playerMapper.toDto(pairingEntity.getSecondPlayer()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public Pairing toEntity(Pair pair, int currentTour, Tournament tournament) {
        Pairing pairing = new Pairing();
        pairing.setFirstPlayer(playerMapper.getEntity(pair.getFirstPlayer().getId()));
        pairing.setSecondPlayer(playerMapper.getEntity(pair.getSecondPlayer().getId()));
        pairing.setTournament(tournament);
        pairing.setCurrentTour(currentTour);
        return pairing;
    }

    public List<Pairing> toEntityList(List<Pair> pairList, int currentTour, Tournament tournament) {
        return pairList.stream()
                .map(pair -> toEntity(pair, currentTour, tournament))
                .collect(Collectors.toList());
    }
}
