package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;

    public void savePlayer(PlayerEntity player) {
        playerRepository.save(player);
    }

    public void saveOpponents(PlayerEntity player, PlayerEntity opponent) {
        player.getOpponents().add(opponent);
        opponent.getOpponents().add(player);
        playerRepository.save(player);
        playerRepository.save(opponent);
    }

    public PlayerEntity getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public Map<Long,PlayerEntity> getPlayers(){
        Long tournamentId = tournamentRepository.getTournamentIdByTourDate(LocalDate.now());
        List<PlayerEntity> players = playerRepository.findAllByTournamentId(tournamentId);
        return players.stream().collect(Collectors.toMap(PlayerEntity::getId, p -> p));
    }

    public Map<String,Long> getPlayersIdByName(){
        Long tournamentId = tournamentRepository.getTournamentIdByTourDate(LocalDate.now());
        List<PlayerEntity> players = playerRepository.findAllByTournamentId(tournamentId);
        return players.stream().collect(Collectors.toMap(PlayerEntity::getName, PlayerEntity::getId));
    }
}