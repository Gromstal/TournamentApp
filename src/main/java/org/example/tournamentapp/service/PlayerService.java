package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.exception.PlayerNotFoundException;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;

    public void savePlayer(PlayerEntity player) {
        playerRepository.save(player);
    }

    @Transactional
    public void saveOpponents(Long playerId, Long opponentId) {
        log.debug("Saving opponents: {} vs {}", playerId, opponentId);
        playerRepository.saveOpponents(playerId, opponentId);
        playerRepository.saveOpponents(opponentId, playerId);
    }

    public PlayerEntity getPlayerById(Long id) {
        return playerRepository.findById(id).orElseThrow(()-> new PlayerNotFoundException("Player not found"));
    }

    public Map<Long,PlayerEntity> getPlayers(Long tournamentId){
        List<PlayerEntity> players = playerRepository.findAllByTournamentId(tournamentId);
        return players.stream()
                .collect(Collectors.toMap( PlayerEntity::getId, player -> player, (existing, replacement) -> { log.warn( "Duplicate player id {} Tournament {}", existing.getId(), tournamentId ); return replacement; } ));
    }

    public PlayerEntity getEntityReference(Long id) {
        return playerRepository.getReferenceById(id);
    }
}