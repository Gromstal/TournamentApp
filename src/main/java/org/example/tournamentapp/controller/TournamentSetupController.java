package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.service.PairingService;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.service.TournamentService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/setup")
@RequiredArgsConstructor
public class TournamentSetupController {

    private final SetupService setupService;
    private final TournamentService tournamentService;
    private final PairingService pairingService;

    @GetMapping()
    public String tournamentSetup(Model model) {
        model.addAttribute("wrapper", setupService.setupPlayerList());
        return "setupPage";
    }

    @PostMapping
    public String makePairs(HttpSession session,
                            @ModelAttribute("wrapper") PlayerListWrapper wrapper,
                            @RequestParam("tourCount") int tourCount,
                            @RequestParam(value = "tourFlag", required = false) String tourFlag) {

        List<Player> playerList = setupService.getPlayerListWithPB(wrapper.getPlayerList());
        Long tournamentId = tournamentService.createTournament(playerList, tourCount);
        playerList = tournamentService.getPlayersDTO();

        if (tourFlag != null) {
            session.setAttribute("players", playerList);
            return "redirect:/hsetup";
        }

        List<Pair> pairs = setupService.createRandomPairList(playerList);
        pairingService.savePairingList(pairs, tournamentId);

        return "redirect:/nextTour";
    }

}
