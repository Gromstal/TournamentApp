package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PairingEntity;
import org.example.tournamentapp.mapper.PairingMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavePairingService {

    private final PlayerService playerService;
    private final PairingRepository pairingRepository;
    private final PairingMapper pairingMapper;
    private final TournamentRepository tournamentRepository;


    public void savePairingList(List<PairDto> pairList, Long tournamentId) {
        int currentTour = tournamentRepository.getCurrentTourById(tournamentId);
        List<PairingEntity> pairingEntities = pairingMapper.toEntityList(pairList, currentTour, tournamentRepository.findById(tournamentId).orElse(null));
        pairingRepository.saveAll(pairingEntities);
    }

    public void saveOpponentsManualSetup(List<PairDto> pairList) {
        Long tournamentId = tournamentRepository.getTournamentIdByTourDate(LocalDate.now());
        int currentTour = tournamentRepository.getCurrentTourById(tournamentId);
        List<PairingEntity> pairingEntities = pairingMapper.toEntityList(pairList, currentTour, tournamentRepository.findById(tournamentId).orElse(null));
        for (PairingEntity pairingEntity : pairingEntities) {
            playerService.saveOpponents(pairingEntity.getFirstPlayer(), pairingEntity.getSecondPlayer());
        }
    }

}
