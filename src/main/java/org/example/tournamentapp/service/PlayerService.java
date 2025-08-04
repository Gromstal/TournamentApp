package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public void saveOpponents(PlayerEntity player, PlayerEntity opponent) {
        player.getOpponents().add(opponent);
        opponent.getOpponents().add(player);
        playerRepository.save(player);
        playerRepository.save(opponent);
    }

    public PlayerEntity getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public void savePlayer(PlayerEntity player) {
        playerRepository.save(player);
    }
}