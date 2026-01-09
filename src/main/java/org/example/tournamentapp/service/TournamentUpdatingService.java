package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentUpdatingService {

    private final TournamentRepository tournamentRepository;
    private final TournamentDataService tournamentDataService;

    @Transactional
    public int updateCurrentTour(Long tournamentId) {
        Tournament tournament = tournamentDataService.getTournamentById(tournamentId);
        tournament.setCurrentTour(tournament.getCurrentTour() + 1);
        tournamentRepository.save(tournament);

        log.info(
                "Tournament {} moved to tour {}",
                tournamentId,
                tournament.getCurrentTour()
        );
        return tournament.getCurrentTour();
    }
}
