package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PairingEntity;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.example.tournamentapp.mapper.PairingMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavePairingService {

    private final PlayerService playerService;
    private final PairingRepository pairingRepository;
    private final PairingMapper pairingMapper;
    private final TournamentRepository tournamentRepository;


    @Transactional
    public void savePairingList(List<PairDto> pairList, Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(()-> new TournamentNotFoundException("Tournament not found with id: "+ tournamentId));

        int currentTour = tournament.getCurrentTour();

        List<PairingEntity> entities = pairList.stream()
                .map(pair -> {
                    PlayerEntity firstRef = playerService.getEntityReference(pair.getFirstPlayer().getId());
                    PlayerEntity secondRef = playerService.getEntityReference(pair.getSecondPlayer().getId());
                    return pairingMapper.toEntity(pair, firstRef, secondRef, currentTour, tournament);
                })
                .toList();

        pairingRepository.saveAll(entities);
    }

    public void saveOpponentsManualSetup(List<PairDto> pairList, Long tournamentId) {
        for (PairDto pair : pairList) {
            Long player = pair.getFirstPlayer().getId();
            Long opponent = pair.getSecondPlayer().getId();
            playerService.saveOpponents(player, opponent);
        }
    }
}
