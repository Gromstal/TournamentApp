package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.model.TournamentHistory;
import org.example.tournamentapp.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentHistoryService {

    private final TournamentRepository tournamentRepository;

    public List<TournamentHistory> getTournamentHistoryList() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        List<TournamentHistory> options = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            TournamentHistory tournamentHistory = new TournamentHistory(tournament.getId(), tournament.getTourDate());
            options.add(tournamentHistory);
        }
        return options;
    }
}
