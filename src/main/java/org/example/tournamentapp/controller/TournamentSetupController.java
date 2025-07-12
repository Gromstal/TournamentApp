package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.Pair;
import org.example.tournamentapp.model.Player;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class TournamentSetupController {

    private final SetupService setupService;

    @GetMapping()
    public String tournamentSetup(Model model) {
        model.addAttribute("wrapper", setupService.setupPlayerList());
        return "setupPage";
    }

    @PostMapping
    public String makePairs(@ModelAttribute("wrapper") PlayerListWrapper wrapper,
                            @RequestParam("tourCount") int tourCount,
                            @RequestParam(value = "tourFlag", required = false) String tourFlag,
                            HttpSession session) {

        session.setAttribute("totalTours", tourCount);
        session.setAttribute("currentTour", 1);

        List<Player> playerList = wrapper.getPlayerList();
        if (playerList.size()%2!=0) {
            playerList.add(setupService.getProxyBot());
        }
        session.setAttribute("playerList", playerList);

        if (tourFlag != null) {
            return "redirect:/hsetup";
        }

        List<Pair> pairs = setupService.createRandomPairList(playerList);
        session.setAttribute("pairs", pairs);

        return "redirect:/nextTour";
    }

}
