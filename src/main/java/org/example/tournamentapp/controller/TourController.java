package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.service.PairingService;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.service.TourService;
import org.example.tournamentapp.service.TournamentService;
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

    private final TourService tourService;
    private final SetupService setupService;
    private final PairingService pairingService;
    private final TournamentService tournamentService;

    @GetMapping
    public String showTourPage(Model model) {
        if (tournamentService.isEnded()) {
            return "redirect:/finalPage";
        }

        int currentTour = tournamentService.getCurrentTourByTourDate(LocalDate.now());
        List<Pair> pairs = pairingService.getPairsFromDB(currentTour);

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(pairs);

        model.addAttribute("pairsWrapper", wrapper);
        model.addAttribute("currentTour", currentTour);

        return "nextTourPage";
    }

    @PostMapping()
    public String calculateScores(@ModelAttribute PairsWrapper pairsWrapper, Model model, HttpSession session) {

        int currentTour = tournamentService.getCurrentTourByTourDate(LocalDate.now());
        int total = tournamentService.getTourCountByTourDate(LocalDate.now());
        List<Pair> pairs = pairingService.getPairsFromDB(currentTour);
        Long tournamentId = tournamentService.getTournamentIdByTourDate(LocalDate.now());

        tourService.mergePairs(pairs, pairsWrapper);
        tourService.calculateTourPoints(new PairsWrapper(pairs));

        if (currentTour == total) {
            tournamentService.saveIsEnded();
            return "redirect:/finalPage";
        }

        List<Pair> newPairs = setupService.createTourPairList(tournamentService.getPlayersDTO());

        int newTour = tournamentService.updateCurrentTour(tournamentId);
        pairingService.savePairingList(newPairs, tournamentId);

        model.addAttribute("pairsWrapper", new PairsWrapper(newPairs));
        model.addAttribute("currentTour", newTour);

        List<Player> players = tournamentService.getPlayersDTO();
        session.setAttribute("players", players);
        session.setAttribute("currentTour", newTour);

        return "redirect:/subTotalResult";
    }
}
