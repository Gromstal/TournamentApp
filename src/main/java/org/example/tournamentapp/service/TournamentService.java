package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.model.*;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;
    private final PairingRepository pairingRepository;
    private final CreatingPairingService creatingPairingService;
    private final MergePairingService mergePairingService;
    private final CalculateService calculateService;
    private final SavePairingService savePairingService;

    public TourContext processingTournament(PairsWrapper pairsWrapper) {

        TournamentContext tournamentContext = getTournamentContext();
        List<PairDto> pairs = mergePairingService.getPairingList(tournamentContext.currentTour());

        mergePairingService.mergePairs(pairs, pairsWrapper);
        calculateService.calculateFromPairsWrapper(new PairsWrapper(pairs));

        if (tournamentContext.currentTour() == tournamentContext.total()) {
            saveIsEnded();
            return new TourContext(true, tournamentContext.currentTour(), List.of(), List.of());
        }

        List<PlayerDto> players = getPlayersDTO();
        List<PairDto> newPairs = creatingPairingService.createTourPairList(players);
        int updatedTour = updateCurrentTour(tournamentContext.tournamentId());
        savePairingService.savePairingList(newPairs, tournamentContext.tournamentId());

        return new TourContext(false, updatedTour, newPairs, players);
    }

    public int updateCurrentTour(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setCurrentTour(tournament.getCurrentTour() + 1);
        tournamentRepository.save(tournament);
        return tournament.getCurrentTour();
    }

    public void saveIsEnded() {
        Tournament tournament = tournamentRepository.getTournamentByTourDate(LocalDate.now()).orElse(null);
        tournament.setTournamentIsEnded(true);
        tournamentRepository.save(tournament);
    }

    public void deleteTournament() {
        List<PlayerEntity> allPlayers = playerRepository.findAll();
        for (PlayerEntity player : allPlayers) {
            player.getOpponents().clear();
        }
        playerRepository.saveAll(allPlayers);

        pairingRepository.deleteAll();
        playerRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    public List<PlayerDto> getPlayersDTO() {
        Long id = getTournamentIdByTourDate(LocalDate.now());
        return playerMapper.toDtoList(getAllPlayersByTournamentId(id));
    }

    public List<LocalDate> getAllTournamentDates() {
        return tournamentRepository.findAll().stream().map(Tournament::getTourDate).collect(Collectors.toList());
    }

    public int getCurrentTourByTourDate(LocalDate tourDate) {
        return tournamentRepository.getCurrentTourByTourDate(tourDate);
    }

    public int getTourCountByTourDate(LocalDate tourDate) {
        return tournamentRepository.getTourCountByTourDate(tourDate);
    }

    public List<PlayerEntity> getAllPlayersByTournamentId(Long tournamentId) {
        return playerRepository.findAllByTournamentId(tournamentId);
    }

    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId).orElse(null);
    }

    public Long getTournamentIdByTourDate(LocalDate tourDate) {
        return tournamentRepository.getTournamentIdByTourDate(LocalDate.now());
    }

    public boolean isEnded() {
        return tournamentRepository.getIsEndedCountByTourDate(LocalDate.now());
    }

    private TournamentContext getTournamentContext() {
        int currentTour = getCurrentTourByTourDate(LocalDate.now());
        int total = getTourCountByTourDate(LocalDate.now());
        Long tournamentId = getTournamentIdByTourDate(LocalDate.now());
        return new TournamentContext(currentTour, total, tournamentId);
    }
}
