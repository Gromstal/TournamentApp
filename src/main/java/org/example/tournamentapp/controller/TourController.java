package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.service.TourService;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/nextTour")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;
    private final SetupService setupService;

    @GetMapping
    public String showTourPage(HttpSession session, Model model) {
        List<Pair> pairs = (List<Pair>) session.getAttribute("pairs");

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(pairs);

        model.addAttribute("pairsWrapper", wrapper);
        model.addAttribute("currentTour", session.getAttribute("currentTour"));

        return "nextTourPage";
    }

    @PostMapping()
    public String calculateScores(@ModelAttribute PairsWrapper pairsWrapper, Model model, HttpSession session) {

        List<Pair> sessionPairs = (List<Pair>) session.getAttribute("pairs");

        int current = (Integer)session.getAttribute("currentTour");
        int total = (Integer)session.getAttribute("totalTours");

        tourService.mergePairs(sessionPairs, pairsWrapper);
        tourService.calculateTourPoints(new PairsWrapper(sessionPairs));

        if (current == total) {
            return "redirect:/finalPage";
        }

        List<Pair> newPairs = setupService.createTourPairList((List<Player>)session.getAttribute("playerList"));

        session.setAttribute("pairs", newPairs);
        model.addAttribute("pairsWrapper", new PairsWrapper(newPairs));

        session.setAttribute("currentTour", (Integer)session.getAttribute("currentTour")+1);
        model.addAttribute("currentTour", session.getAttribute("currentTour"));

        return "redirect:/subTotalResult";
    }
}
