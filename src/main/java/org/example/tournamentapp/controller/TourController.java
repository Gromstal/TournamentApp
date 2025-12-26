package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.TourContext;
import org.example.tournamentapp.service.*;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/nextTour")
@RequiredArgsConstructor
public class TourController {

    private final MergePairingService mergePairingService;
    private final TournamentService tournamentService;

    @GetMapping
    public String showTourPage(Model model) {
        if (tournamentService.isEnded()) {
            return "redirect:/finalPage";
        }

        int currentTour = tournamentService.getCurrentTourByTourDate(LocalDate.now());
        List<PairDto> pairs = mergePairingService.getPairingList(currentTour);

        PairsWrapper wrapper = new PairsWrapper(pairs);

        model.addAttribute("pairsWrapper", wrapper);
        model.addAttribute("currentTour", currentTour);

        return "nextTourPage";
    }

    @PostMapping()
    public String calculateScores(@ModelAttribute PairsWrapper pairsWrapper, Model model, HttpSession session) {
        TourContext tourContext = tournamentService.processingTournament(pairsWrapper);

        if (tourContext.ended()) {
            return "redirect:/finalPage";
        }

        model.addAttribute("pairsWrapper", new PairsWrapper(tourContext.newPairs()));
        model.addAttribute("currentTour", tourContext.updatedTour());

        session.setAttribute("players", tourContext.players());
        session.setAttribute("currentTour", tourContext.updatedTour());

        return "redirect:/subTotalResult";
    }
}
