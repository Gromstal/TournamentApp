package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.mapper.PairingMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MergePairingService {

    private final PairingRepository pairingRepository;
    private final PairingMapper pairingMapper;

    public List<PairDto> getPairingList(Map<Long, Set<String>> opponentsMap,Long tournamentId, int currentTour) {
        return pairingMapper.toDtoList(opponentsMap,pairingRepository.findByTournament_IdAndCurrentTour(tournamentId,currentTour));
    }

    public void mergePairs(List<PairDto> sessionPairs, PairsWrapper pairsWrapper) {
        log.debug(
                "Merging pair results: sessionPairs={}, formPairs={}",
                sessionPairs.size(),
                pairsWrapper.getPairs().size()
        );
        IntStream.range(0, sessionPairs.size()).forEach(i -> {
            PairDto sessionPair = sessionPairs.get(i);
            PairDto formPair = pairsWrapper.getPairs().get(i);

            sessionPair.getFirstPlayer().setMp(formPair.getFirstPlayer().getMp());
            sessionPair.getFirstPlayer().setAp(formPair.getFirstPlayer().getAp());
            sessionPair.getSecondPlayer().setMp(formPair.getSecondPlayer().getMp());
            sessionPair.getSecondPlayer().setAp(formPair.getSecondPlayer().getAp());
        });
    }
}
