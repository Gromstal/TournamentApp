package org.example.tournamentapp.service;

import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Service;


import java.util.Comparator;
import java.util.List;

@Service
public class SortingPlayerService {

    public List<PlayerDto> getSortedPlayerList(List<PlayerDto> setupList) {
        return setupList.stream()
                .filter(p -> p.getName() != null && !p.getName().isEmpty())
                .sorted(
                        Comparator.comparingInt(PlayerDto::getTp).reversed()
                                .thenComparing(Comparator.comparingInt(PlayerDto::getVp).reversed())
                                .thenComparing((PlayerDto::getVpOpp))
                                .thenComparing(Comparator.comparingInt(PlayerDto::getTotalMp).reversed())
                                .thenComparing(Comparator.comparingInt(PlayerDto::getTotalAp).reversed())
                )
                .toList();
    }
}