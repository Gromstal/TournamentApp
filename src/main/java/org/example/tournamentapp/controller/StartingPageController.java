package org.example.tournamentapp.controller;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.service.TournamentService;
import org.example.tournamentapp.wrapper.DatesWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class StartingPageController {

    private final TournamentService tournamentService;

    @GetMapping
    public String startingPage(Model model) {
        DatesWrapper wrapper = new DatesWrapper();
        wrapper.setDates(tournamentService.getAllTournamentDates());
        model.addAttribute("tournaments", wrapper);
        return "startingPage";
    }

    @PostMapping
    public String startingPageSubmit() {

            return "redirect:/nextTour";
    }
}
