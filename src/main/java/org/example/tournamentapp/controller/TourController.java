package org.example.tournamentapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.record.ProcessingOption;
import org.example.tournamentapp.model.record.TourContext;
import org.example.tournamentapp.service.TournamentFinishingService;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.service.TournamentProcessingService;
import org.example.tournamentapp.validation.ScoreGroup;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/nextTour")
@RequiredArgsConstructor
@Slf4j
public class TourController {

    private final TournamentProcessingService tournamentProcessingService;
    private final TournamentFinishingService tournamentFinishingService;
    private final TournamentDataService tournamentDataService;

    @GetMapping
    public String showTourPage(Model model,
                               @RequestParam Long tournamentId,
                               RedirectAttributes redirectAttributes) {
        if (tournamentFinishingService.isEnded(tournamentId)) {
            redirectAttributes.addAttribute("tournamentId", tournamentId);
            return "redirect:/finalPage";
        }

        ProcessingOption processingOption = tournamentProcessingService.getProcessingOption(tournamentId);

        model.addAttribute("pairsWrapper", processingOption.wrapper());
        model.addAttribute("currentTour", processingOption.currentTour());
        model.addAttribute("tournamentId", tournamentId);

        return "nextTourPage";
    }

    @PostMapping()
    public String calculateScores(@Validated(ScoreGroup.class) @ModelAttribute("pairsWrapper") PairsWrapper pairsWrapper,
                                  BindingResult bindingResult,
                                  Model model,
                                  @RequestParam Long tournamentId,
                                  RedirectAttributes redirectAttributes) {
        log.info("POST /nextTour. Calculate score requested for Tournament {}", tournamentId);

        if (bindingResult.hasErrors()) {
            int currentTour = tournamentDataService.getCurrentTourByTournamentId(tournamentId);
            model.addAttribute("pairsWrapper", pairsWrapper);
            model.addAttribute("currentTour", currentTour);
            model.addAttribute("tournamentId", tournamentId);
            return "nextTourPage";
        }

        TourContext tourContext = tournamentProcessingService.processingTournament(tournamentId, pairsWrapper);

        if (tourContext.ended()) {
            redirectAttributes.addAttribute("tournamentId", tournamentId);
            return "redirect:/finalPage";
        }

        redirectAttributes.addAttribute("tournamentId", tournamentId);
        return "redirect:/subTotalResult";
    }
}
