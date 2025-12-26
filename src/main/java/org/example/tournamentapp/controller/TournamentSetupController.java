package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.service.*;
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
    private final TournamentCreatingService tournamentCreatingService;


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

        List<PlayerDto> playerDtoList = setupService.setupPlayerListWithProxyBot(wrapper.getPlayerList());
        Long tournamentId = tournamentCreatingService.createTournament(playerDtoList, tourCount);
        playerDtoList = tournamentService.getPlayersDTO();

        if (tourFlag != null) {
            session.setAttribute("players", playerDtoList);
            return "redirect:/hsetup";
        }

        tournamentCreatingService.saveStartingPairingList(playerDtoList, tournamentId);

        return "redirect:/nextTour";
    }

}
