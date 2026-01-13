package org.example.tournamentapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.ManualSetupOption;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.service.TournamentCreatingService;
import org.example.tournamentapp.validation.CreateGroup;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/setup")
@RequiredArgsConstructor
@Slf4j
public class TournamentSetupController {

    private final SetupService setupService;
    private final TournamentCreatingService tournamentCreatingService;


    @GetMapping()
    public String tournamentSetup(Model model) {
        model.addAttribute("wrapper", setupService.setupPlayerList());
        return "setupPage";
    }

    @PostMapping
    public String makePairs(@Validated(CreateGroup.class) @ModelAttribute("wrapper") PlayerListWrapper wrapper,
                            BindingResult bindingResult,
                            @RequestParam("tourCount") int tourCount,
                            @RequestParam(value = "tourFlag", defaultValue = "false") boolean manualSetup,
                            RedirectAttributes redirectAttributes) {

        log.info("POST /setup. Tournament setup requested. Manual pairing setup: {}", manualSetup);

        if (bindingResult.hasErrors()) {
            logValidationErrors(bindingResult);
            return "setupPage";
        }

        List<PlayerDto> cleaned = wrapper.getPlayerList().stream()
                .filter(p -> p.getName() != null && !p.getName().trim().isEmpty())
                .toList();

        if (cleaned.size() < 2) {
            bindingResult.rejectValue("playerList", "minPlayers", "Нужно минимум 2 игрока");
            logValidationErrors(bindingResult);
            return "setupPage";
        }

        ManualSetupOption manualSetupOption = tournamentCreatingService.create(cleaned, tourCount, manualSetup);

        redirectAttributes.addAttribute("tournamentId", manualSetupOption.tournamentId());
        return manualSetupOption.isManual() ? "redirect:/hsetup" : "redirect:/nextTour";
    }

    private void logValidationErrors(BindingResult bindingResult) {
        bindingResult.getAllErrors().forEach(error -> log.warn("Validation error: {}", error));
    }


}
