package org.example.tournamentapp.controller;


import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.service.TourService;
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
    private final TourService tourService;

    @GetMapping
    public String finalPage(Model model) {
        PlayerListWrapper wrapper = new PlayerListWrapper();
        wrapper.setPlayerList(tourService.getFinalSortedList(tournamentService.getPlayersDTO()));
        model.addAttribute("wrapper", wrapper);
        return "finalPage";
    }

    @PostMapping
    public String finalPageSubmit() {
        tournamentService.deleteTournament();
        return "redirect:/";
    }
}
