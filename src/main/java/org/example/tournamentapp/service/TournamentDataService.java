package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.TournamentContext;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDataService {

    private final TournamentOpponentHistoryService tournamentOpponentHistoryService;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Cacheable(value = "tournament", key = "#tournamentId")
    public Tournament getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(()-> new TournamentNotFoundException("Tournament " + tournamentId + " not found"));
    }

    public int getCurrentTourByTournamentId(Long tournamentId) {
        return tournamentRepository.getCurrentTourById(tournamentId);
    }

    public TournamentContext getTournamentContext(Long tournamentId) {
        int currentTour = getCurrentTourByTournamentId(tournamentId);
        int total = getTourCountById(tournamentId);

        return new TournamentContext(currentTour, total, tournamentId);
    }

    private int getTourCountById(Long tournamentId) {
        return tournamentRepository.getTourCountById(tournamentId);
    }

    public List<PlayerDto> getPlayersDTO(Long tournamentId) {
        return playerMapper.toDtoList(tournamentOpponentHistoryService.getOpponentsMap(tournamentId),getAllPlayersByTournamentId(tournamentId));
    }

    public List<PlayerEntity> getAllPlayersByTournamentId(Long tournamentId) {
        return playerRepository.findAllByTournamentId(tournamentId);
    }



}
