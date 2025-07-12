package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hsetup")
@RequiredArgsConstructor
public class HandSetupController {

    private final SetupService setupService;


    @GetMapping()
    public String tournamentHandSetup(Model model, HttpSession session) {
        List<Player> playerList = (List<Player>) session.getAttribute("playerList");
        List<Pair> pairs = setupService.createHandPairList(playerList);

        PairsWrapper pairsWrapper = new PairsWrapper(pairs);

        model.addAttribute("pairsWrapper", pairsWrapper);
        model.addAttribute("playerList", playerList);

        return "handSetupPage";
    }

    @PostMapping
    public String makeHandPairs(@ModelAttribute("pairsWrapper") PairsWrapper wrapper, HttpSession session) {
        List<Pair> pairs = wrapper.getPairs();
        session.setAttribute("pairs", pairs);

        List<Player> updatedPlayers = setupService.extractPlayersFromPairs(pairs);
        session.setAttribute("playerList", updatedPlayers);

        return "redirect:/nextTour";
    }
}
