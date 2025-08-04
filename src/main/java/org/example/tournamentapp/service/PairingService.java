package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.Pairing;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.mapper.PairingMapper;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.repository.PairingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PairingService {

    private final TournamentService tournamentService;
    private final PairingRepository pairingRepository;
    private final PairingMapper pairingMapper;
    private final PlayerService playerService;

    public List<Pair> getPairsFromDB(int currentTour){
        return pairingMapper.toDtoList(pairingRepository.findByCurrentTour(currentTour));
    }

    public void savePairingList(List<Pair> pairList, Long tournamentId){
        int currentTour = tournamentService.getCurrentTourByTournamentId(tournamentId);
        System.out.println(pairList);
        List<Pairing> pairings = pairingMapper.toEntityList(pairList,currentTour,tournamentService.getTournamentById(tournamentId));
        pairingRepository.saveAll(pairings);
    }

    public void saveOpponentsFromHandSetup(List<Pair> pairList){
        Long tournamentId = tournamentService.getTournamentIdByTourDate(LocalDate.now());
        int currentTour = tournamentService.getCurrentTourByTournamentId(tournamentId);
        List<Pairing> pairings = pairingMapper.toEntityList(pairList,currentTour,tournamentService.getTournamentById(tournamentId));
        for (Pairing pairing : pairings) {
            playerService.saveOpponents(pairing.getFirstPlayer(), pairing.getSecondPlayer());
        }
    }

    public List<Pair> syncPairs (List<Pair> pairList){
        Map<String, Long> players = getPlayersIdByName();
        List<Pair> syncList = new ArrayList<>();
        for (Pair pair : pairList) {
            pair.getFirstPlayer().setId(players.get(pair.getFirstPlayer().getName()));
            pair.getSecondPlayer().setId(players.get(pair.getSecondPlayer().getName()));
            syncList.add(pair);
        }
        return syncList;
    }

    private Map<String,Long> getPlayersIdByName(){
        Long tournamentId = tournamentService.getTournamentIdByTourDate(LocalDate.now());
        List<PlayerEntity> players = tournamentService.getAllPlayersByTournamentId(tournamentId);
        return players.stream().collect(Collectors.toMap(PlayerEntity::getName, PlayerEntity::getId));
    }
}
