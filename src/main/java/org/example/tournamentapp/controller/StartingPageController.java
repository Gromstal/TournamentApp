package org.example.tournamentapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.TournamentHistory;
import org.example.tournamentapp.service.TournamentHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class StartingPageController {

    private final TournamentHistoryService tournamentHistoryService;

    @GetMapping
    public String startingPage(Model model) {
        List<TournamentHistory> tournaments = tournamentHistoryService.getTournamentHistoryList();
        model.addAttribute("tournaments", tournaments);
        return "startingPage";
    }

    @PostMapping
    public String startingPageSubmit(@RequestParam Long tournamentId,
                                     RedirectAttributes redirectAttributes) {
        log.info("POST /. Choosing tournament requested for Tournament {}", tournamentId);
        redirectAttributes.addAttribute("tournamentId", tournamentId);
        return "redirect:/nextTour";
    }
}
