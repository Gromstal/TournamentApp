package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.service.*;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hsetup")
@RequiredArgsConstructor
public class ManualSetupController {

    private final PairingService pairingService;
    private final CreatingPairingService creatingPairingService;
    private final ManualSetupService manualSetupService;


    @GetMapping()
    public String manualSetup(Model model, HttpSession session) {
        List<PlayerDto> playerDtoList = (List<PlayerDto>) session.getAttribute("players");
        List<PairDto> pairs = creatingPairingService.createManualPairList(playerDtoList);

        PairsWrapper pairsWrapper = new PairsWrapper(pairs);

        model.addAttribute("pairsWrapper", pairsWrapper);
        model.addAttribute("playerList", playerDtoList);

        return "handSetupPage";
    }

    @PostMapping
    public String saveManualPairing(@ModelAttribute("pairsWrapper") PairsWrapper wrapper, HttpSession session) {
        List<PairDto> pairs = pairingService.syncPairs(wrapper.getPairs());
        session.setAttribute("pairs", pairs);
        manualSetupService.saveManualSetup(pairs);

        return "redirect:/nextTour";
    }
}
