package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.builder.TournamentBuilder;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentBuilder tournamentBuilder;
    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;
    private final PairingRepository pairingRepository;

    public Long createTournament(List<Player> players, int tourCount ) {
        Tournament newTournament = tournamentBuilder.getNewTournament(
                playerMapper.toEntityList(players),
                tourCount);
        newTournament.getPlayers().forEach(playerEntity -> playerEntity.setTournament(newTournament));
        tournamentRepository.save(newTournament);

        return getTournamentIdByTourDate(LocalDate.now());
    }

    public List<LocalDate> getAllTournamentDates() {
        return tournamentRepository.findAll().stream().map(Tournament::getTourDate).collect(Collectors.toList());
    }

    public int getCurrentTourByTournamentId (Long tournamentId) {
       return tournamentRepository.getCurrentTourById(tournamentId);
    }

    public int getCurrentTourByTourDate (LocalDate tourDate) {
        return tournamentRepository.getCurrentTourByTourDate(tourDate);
    }

    public int getTourCountByTourDate (LocalDate tourDate) {
        return tournamentRepository.getTourCountByTourDate(tourDate);
    }

    public List<PlayerEntity> getAllPlayersByTournamentId(Long tournamentId) {
        return playerRepository.findAllByTournamentId(tournamentId);
    }

    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId).orElse(null);
    }

    public Long getTournamentIdByTourDate(LocalDate tourDate) {
        return  tournamentRepository.getTournamentIdByTourDate(LocalDate.now());
    }

    public int updateCurrentTour(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        tournament.setCurrentTour(tournament.getCurrentTour()+1);
        tournamentRepository.save(tournament);
        return tournament.getCurrentTour();
    }

    public List<Player> getPlayersDTO() {
        Long id = getTournamentIdByTourDate(LocalDate.now());
        return playerMapper.toDtoList(getAllPlayersByTournamentId(id));
    }

    public boolean isEnded() {
        return tournamentRepository.getIsEndedCountByTourDate(LocalDate.now());
    }

    public void saveIsEnded() {
       Tournament tournament= tournamentRepository.getTournamentByTourDate(LocalDate.now()).orElse(null);
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
}
