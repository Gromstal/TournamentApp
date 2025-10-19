package org.example.tournamentapp.mapper;


import lombok.Data;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.repository.PlayerRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Component
public class PlayerMapper {

    private final PlayerRepository playerRepository;

    public Player toDto(PlayerEntity playerEntity) {
        return getPlayer(playerEntity);
    }

    public List<Player> toDtoList(List<PlayerEntity> entityList) {
        return entityList.stream()
                .map(this::getPlayer)
                .collect(Collectors.toList());
    }

    public PlayerEntity toEntity(Player player) {
        PlayerEntity playerEntity = new PlayerEntity();

        playerEntity.setName(player.getName());
        playerEntity.setFaction(player.getFaction());

        List<PlayerEntity> opponentsEntities = player.getNamesPlayed().stream()
                .map(name -> playerRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Player not found: " + name)))
                .collect(Collectors.toList());

        playerEntity.setOpponents(opponentsEntities);

        playerEntity.setId(player.getId());
        playerEntity.setAp(player.getAp());
        playerEntity.setMp(player.getMp());
        playerEntity.setVp(player.getVp());
        playerEntity.setTp(player.getTp());
        playerEntity.setTotalAp(player.getTotalAp());
        playerEntity.setTotalMp(player.getTotalMp());
        playerEntity.setVpOpp(player.getVpOpp());

        return playerEntity;
    }

    public List<PlayerEntity> toEntityList(List<Player> players) {
        return players.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public PlayerEntity getEntity(Long id) {
        return playerRepository.findById(id).get();
    }

    private Player getPlayer(PlayerEntity playerEntity) {
        List<String> opponentsName = Optional.ofNullable(playerEntity.getOpponents())
                .orElse(Collections.emptyList())
                .stream()
                .map(PlayerEntity::getName)
                .collect(Collectors.toList());
        return Player.builder()
                .id(playerEntity.getId())
                .name(playerEntity.getName())
                .faction(playerEntity.getFaction())
                .namesPlayed(opponentsName)
                .inPair(false)
                .tp(playerEntity.getTp())
                .vp(playerEntity.getVp())
                .mp(playerEntity.getMp())
                .ap(playerEntity.getAp())
                .totalMp(playerEntity.getTotalMp())
                .totalAp(playerEntity.getTotalAp())
                .vpOpp(playerEntity.getVpOpp())
                .build();
    }
}
