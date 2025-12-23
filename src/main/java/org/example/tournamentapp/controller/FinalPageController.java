package org.example.tournamentapp.controller;


import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.service.SortingPlayerService;
import org.example.tournamentapp.service.TournamentService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping ("/finalPage")
@RequiredArgsConstructor
public class FinalPageController {

    private final TournamentService tournamentService;
    private final SortingPlayerService sortingPlayerService;

    @GetMapping
    public String finalPage(Model model) {
        PlayerListWrapper wrapper = new PlayerListWrapper();
        wrapper.setPlayerList(sortingPlayerService.getSortedPlayerList(tournamentService.getPlayersDTO()));
        model.addAttribute("wrapper", wrapper);
        return "finalPage";
    }

    @PostMapping
    public String finalPageSubmit() {
        tournamentService.deleteTournament();
        return "redirect:/";
    }
}
