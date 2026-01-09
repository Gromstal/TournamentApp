package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.example.tournamentapp.repository.PairingRepository;
import org.example.tournamentapp.repository.PlayerRepository;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishingService {

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final PairingRepository pairingRepository;

    @Transactional
    public void deleteTournament(Long tournamentId) {
        playerRepository.deleteOpponentsLinksByTournamentId(tournamentId);
        pairingRepository.deleteByTournament_Id(tournamentId);
        playerRepository.deleteByTournament_Id(tournamentId);
        tournamentRepository.deleteById(tournamentId);

        log.info("Tournament {} deleted", tournamentId);
    }

    public boolean isEnded(Long tournamentId) {
        return tournamentRepository.getIsEndedById(tournamentId);
    }

    public void saveIsEnded(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TournamentNotFoundException("Tournament not found with id: " + tournamentId));
        tournament.setTournamentIsEnded(true);
        tournamentRepository.save(tournament);

        log.info("Tournament {} is ended", tournamentId);
    }
}
