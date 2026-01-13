package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TournamentOpponentHistoryService {

    private final PlayerRepository playerRepository;

    public Map<Long, Set<String>> getOpponentsMap(Long tournamentId) {
        List<Object[]> rows = playerRepository.findOpponentNames(tournamentId);

        Map<Long, Set<String>> map = new HashMap<>();
        for (Object[] row : rows) {
            Long playerId = ((Number) row[0]).longValue();
            String oppName = (String) row[1];

            map.computeIfAbsent(playerId, k -> new HashSet<>()).add(oppName);
        }
        return map;
    }

}
