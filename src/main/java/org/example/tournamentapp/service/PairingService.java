package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PairingEntity;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.mapper.PairingMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PairingService {

    private final TournamentService tournamentService;
    private final PairingRepository pairingRepository;
    private final PairingMapper pairingMapper;
    private final PlayerService playerService;

    public List<PairDto> getPairingList(int currentTour){
        return pairingMapper.toDtoList(pairingRepository.findByCurrentTour(currentTour));
    }

    public void savePairingList(List<PairDto> pairList, Long tournamentId){
        int currentTour = tournamentService.getCurrentTourByTournamentId(tournamentId);
        System.out.println(pairList);
        List<PairingEntity> pairingEntities = pairingMapper.toEntityList(pairList,currentTour,tournamentService.getTournamentById(tournamentId));
        pairingRepository.saveAll(pairingEntities);
    }

    public void saveOpponentsManualSetup(List<PairDto> pairList){
        Long tournamentId = tournamentService.getTournamentIdByTourDate(LocalDate.now());
        int currentTour = tournamentService.getCurrentTourByTournamentId(tournamentId);
        List<PairingEntity> pairingEntities = pairingMapper.toEntityList(pairList,currentTour,tournamentService.getTournamentById(tournamentId));
        for (PairingEntity pairingEntity : pairingEntities) {
            playerService.saveOpponents(pairingEntity.getFirstPlayer(), pairingEntity.getSecondPlayer());
        }
    }

    public void mergePairs(List<PairDto> sessionPairs, PairsWrapper pairsWrapper) {
        IntStream.range(0, sessionPairs.size()).forEach(i -> {
            PairDto sessionPair = sessionPairs.get(i);
            PairDto formPair = pairsWrapper.getPairs().get(i);

            sessionPair.getFirstPlayer().setMp(formPair.getFirstPlayer().getMp());
            sessionPair.getFirstPlayer().setAp(formPair.getFirstPlayer().getAp());
            sessionPair.getSecondPlayer().setMp(formPair.getSecondPlayer().getMp());
            sessionPair.getSecondPlayer().setAp(formPair.getSecondPlayer().getAp());
        });
    }

    public List<PairDto> syncPairs (List<PairDto> pairList){
        Map<String, Long> players = getPlayersIdByName();
        List<PairDto> syncList = new ArrayList<>();
        for (PairDto pair : pairList) {
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
