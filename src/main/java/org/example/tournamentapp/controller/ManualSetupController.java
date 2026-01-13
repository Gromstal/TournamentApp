package org.example.tournamentapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.record.ManualPairsSetupOption;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.service.CreatingPairingService;
import org.example.tournamentapp.service.ManualSetupService;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.validation.SelectGroup;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/hsetup")
@RequiredArgsConstructor
@Slf4j
public class ManualSetupController {

    private final CreatingPairingService creatingPairingService;
    private final ManualSetupService manualSetupService;
    private final TournamentDataService tournamentDataService;


    @GetMapping()
    public String getManualSetup(Model model,
                                 @RequestParam Long tournamentId) {
        ManualPairsSetupOption manualSetupOption = manualSetupService.getSetupPairWrapper(tournamentId);

        model.addAttribute("pairsWrapper", manualSetupOption.pairsWrapper());
        model.addAttribute("playerList", manualSetupOption.playerDtoList());
        model.addAttribute("tournamentId", tournamentId);

        return "handSetupPage";
    }

    @PostMapping
    public String saveManualPairing(@Validated(SelectGroup.class) @ModelAttribute("pairsWrapper") PairsWrapper wrapper,
                                    BindingResult bindingResult,
                                    Model model,
                                    @RequestParam Long tournamentId,
                                    RedirectAttributes redirectAttributes) {
        log.info("POST /hsetup. Save starting manual pairing requested for Tournament {} ", tournamentId);

        if (bindingResult.hasErrors()) {
            List<PlayerDto> playerDtoList = tournamentDataService.getPlayersDTO(tournamentId);
            if (wrapper.getPairs() == null || wrapper.getPairs().isEmpty()) {
                List<PairDto> pairs = creatingPairingService.createManualPairList(playerDtoList);
                wrapper.setPairs(pairs);
            }

            log.warn("Validation errors in manual setup: {}",
                    bindingResult.getAllErrors());
            model.addAttribute("playerList", playerDtoList);
            model.addAttribute("tournamentId", tournamentId);
            return "handSetupPage";
        }
        manualSetupService.saveManualSetup(tournamentId,wrapper.getPairs());

        redirectAttributes.addAttribute("tournamentId", tournamentId);
        return "redirect:/nextTour";
    }
}
