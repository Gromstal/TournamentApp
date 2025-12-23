package org.example.tournamentapp.mapper;


import lombok.Data;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.PlayerDto;
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

    public PlayerDto toDto(PlayerEntity playerEntity) {
        return getPlayer(playerEntity);
    }

    public List<PlayerDto> toDtoList(List<PlayerEntity> entityList) {
        return entityList.stream()
                .map(this::getPlayer)
                .collect(Collectors.toList());
    }

    public PlayerEntity toEntity(PlayerDto playerDto) {
        PlayerEntity playerEntity = new PlayerEntity();

        playerEntity.setName(playerDto.getName());
        playerEntity.setFaction(playerDto.getFaction());

        List<PlayerEntity> opponentsEntities = playerDto.getNamesPlayed().stream()
                .map(name -> playerRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Player not found: " + name)))
                .collect(Collectors.toList());

        playerEntity.setOpponents(opponentsEntities);

        playerEntity.setId(playerDto.getId());
        playerEntity.setAp(playerDto.getAp());
        playerEntity.setMp(playerDto.getMp());
        playerEntity.setVp(playerDto.getVp());
        playerEntity.setTp(playerDto.getTp());
        playerEntity.setTotalAp(playerDto.getTotalAp());
        playerEntity.setTotalMp(playerDto.getTotalMp());
        playerEntity.setVpOpp(playerDto.getVpOpp());

        return playerEntity;
    }

    public List<PlayerEntity> toEntityList(List<PlayerDto> playerDtos) {
        return playerDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public PlayerEntity getEntity(Long id) {
        return playerRepository.findById(id).get();
    }

    private PlayerDto getPlayer(PlayerEntity playerEntity) {
        List<String> opponentsName = Optional.ofNullable(playerEntity.getOpponents())
                .orElse(Collections.emptyList())
                .stream()
                .map(PlayerEntity::getName)
                .collect(Collectors.toList());
        return PlayerDto.builder()
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
