package org.example.tournamentapp.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.builder.TournamentBuilder;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.mapper.PlayerMapper;
import org.example.tournamentapp.model.record.ManualSetupOption;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentCreatingService {

    private final TournamentBuilder tournamentBuilder;
    private final PlayerMapper playerMapper;
    private final TournamentRepository tournamentRepository;
    private final CreatingPairingService creatingPairingService;
    private final SavePairingService savePairingService;
    private final SetupService setupService;

    @Transactional
    public ManualSetupOption create(List<PlayerDto> playerDtoList, int tourCount, boolean manualSetup) {
        List<PlayerDto> players = setupService.setupPlayerListWithProxyBot(playerDtoList);
        Tournament newTournament = tournamentBuilder.getNewTournament(
                playerMapper.getStartingEntityList(players),
                tourCount);
        newTournament.getPlayers().forEach(playerEntity -> playerEntity.setTournament(newTournament));
        Tournament savedTournament = tournamentRepository.save(newTournament);

        log.info("Tournament {} started with {} players", savedTournament.getId(), savedTournament.getPlayers().size());

        if (manualSetup) {
            return new ManualSetupOption(true,savedTournament.getId());
        }

        List<PlayerDto> savedPlayersDto = savedTournament.getPlayers().stream()
                .map(player -> playerMapper.toDto(new HashSet<>(), player))
                .toList();

        List<PairDto> pairs = creatingPairingService.createRandomPairList(savedTournament.getId(),savedPlayersDto);
        savePairingService.savePairingList(pairs, savedTournament.getId());

        log.info("Random pairs saved successfully");

        return new ManualSetupOption(false,savedTournament.getId());
    }
}
