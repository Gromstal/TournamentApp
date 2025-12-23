package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.Tour;
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

    private final PairingService pairingService;
    private final TournamentService tournamentService;
    private final CalculateService calculateService;
    private final CreatingPairingService creatingPairingService;

    @GetMapping
    public String showTourPage(Model model) {
        if (tournamentService.isEnded()) {
            return "redirect:/finalPage";
        }

        int currentTour = tournamentService.getCurrentTourByTourDate(LocalDate.now());
        List<PairDto> pairs = pairingService.getPairingList(currentTour);

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(pairs);

        model.addAttribute("pairsWrapper", wrapper);
        model.addAttribute("currentTour", currentTour);

        return "nextTourPage";
    }

    @PostMapping()
    public String calculateScores(@ModelAttribute PairsWrapper pairsWrapper, Model model, HttpSession session) {

        Tour tour = tournamentService.processingTournament(pairsWrapper);

        if (tour.ended()) {
            return "redirect:/finalPage";
        }

        model.addAttribute("pairsWrapper", new PairsWrapper(tour.newPairs()));
        model.addAttribute("currentTour", tour.updatedTour());

        session.setAttribute("players", tour.players());
        session.setAttribute("currentTour", tour.updatedTour());

        return "redirect:/subTotalResult";
    }
}
