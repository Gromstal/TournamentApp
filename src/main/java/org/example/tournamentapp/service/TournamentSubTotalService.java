package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.Tournament;
import org.example.tournamentapp.model.record.TournamentSubTotalOption;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentSubTotalService {

    private final SortingPlayerService sortingPlayerService;
    private final TournamentDataService tournamentDataService;

    public TournamentSubTotalOption getTournamentSubTotalOption (Long tournamentId) {
        Tournament tournament =tournamentDataService.getTournamentById(tournamentId);

        PlayerListWrapper wrapper = new PlayerListWrapper();
        wrapper.setPlayerList(sortingPlayerService.getSortedPlayerList(tournamentDataService.getPlayersDTO(tournamentId)));

        return new TournamentSubTotalOption(tournament.getCurrentTour(),wrapper);
    }
}
