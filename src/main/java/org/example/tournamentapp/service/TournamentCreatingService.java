package org.example.tournamentapp.service;


import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.builder.TournamentBuilder;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentCreatingService {

    private final TournamentBuilder tournamentBuilder;
    private final PlayerMapper playerMapper;
    private final TournamentRepository tournamentRepository;
    private final TournamentService tournamentService;
    private final CreatingPairingService creatingPairingService;
    private final SavePairingService savePairingService;

    public Long createTournament(List<PlayerDto> playerDtos, int tourCount) {
        Tournament newTournament = tournamentBuilder.getNewTournament(
                playerMapper.toEntityList(playerDtos),
                tourCount);
        newTournament.getPlayers().forEach(playerEntity -> playerEntity.setTournament(newTournament));
        tournamentRepository.save(newTournament);

        return tournamentService.getTournamentIdByTourDate(LocalDate.now());
    }

    public void saveStartingPairingList(List<PlayerDto> playerDtoList, Long tournamentId) {
        List<PairDto> pairs = creatingPairingService.createRandomPairList(playerDtoList);
        savePairingService.savePairingList(pairs, tournamentId);
    }

}
