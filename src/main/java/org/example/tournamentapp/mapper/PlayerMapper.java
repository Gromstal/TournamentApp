package org.example.tournamentapp.mapper;


import lombok.Data;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import java.util.stream.Collectors;

@Data
@Component
public class PlayerMapper {

    public PlayerDto toDto(Set<String> opponents, PlayerEntity playerEntity) {
        return getPlayer(opponents,playerEntity);
    }

    public List<PlayerDto> toDtoList(Map<Long, Set<String>> opponentsMap,List<PlayerEntity> entityList) {
        return  entityList.stream()
                .map(player-> toDto(opponentsMap.get(player.getId()),player))
                .toList();
    }

    public PlayerEntity getStartingEntity(PlayerDto playerDto) {
        PlayerEntity playerEntity = new PlayerEntity();

        playerEntity.setName(playerDto.getName());
        playerEntity.setFaction(playerDto.getFaction());
        playerEntity.setOpponents(new HashSet<>());
        playerEntity.setAp(0);
        playerEntity.setMp(0);
        playerEntity.setVp(0);
        playerEntity.setTp(0);
        playerEntity.setTotalAp(0);
        playerEntity.setTotalMp(0);
        playerEntity.setVpOpp(0);

        return playerEntity;
    }

    public List<PlayerEntity> getStartingEntityList(List<PlayerDto> playerDtoList) {
        return playerDtoList.stream()
                .map(this::getStartingEntity)
                .collect(Collectors.toList());
    }

    private PlayerDto getPlayer(Set<String> opponentsName,PlayerEntity playerEntity) {
        Set<String> opponents = (opponentsName == null) ? new HashSet<>() : new HashSet<>(opponentsName);

        return PlayerDto.builder()
                .id(playerEntity.getId())
                .name(playerEntity.getName())
                .faction(playerEntity.getFaction())
                .namesPlayed(opponents)
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