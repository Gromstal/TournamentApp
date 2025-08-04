package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.service.PairingService;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.service.TournamentService;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/hsetup")
@RequiredArgsConstructor
public class HandSetupController {

    private final SetupService setupService;
    private final PairingService pairingService;
    private final TournamentService tournamentService;


    @GetMapping()
    public String tournamentHandSetup(Model model, HttpSession session) {
        List<Player> playerList = (List<Player>) session.getAttribute("players");
        List<Pair> pairs = setupService.createHandPairList(playerList);

        PairsWrapper pairsWrapper = new PairsWrapper(pairs);

        model.addAttribute("pairsWrapper", pairsWrapper);
        model.addAttribute("playerList", playerList);

        return "handSetupPage";
    }

    @PostMapping
    public String makeHandPairs(@ModelAttribute("pairsWrapper") PairsWrapper wrapper, HttpSession session) {
        List<Pair> pairs = pairingService.syncPairs(wrapper.getPairs());

        session.setAttribute("pairs", pairs);

        Long id = tournamentService.getTournamentIdByTourDate(LocalDate.now());
        pairingService.savePairingList(pairs, id);
        pairingService.saveOpponentsFromHandSetup(pairs);

        return "redirect:/nextTour";
    }
}
