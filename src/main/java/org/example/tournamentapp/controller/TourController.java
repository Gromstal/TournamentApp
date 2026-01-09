package org.example.tournamentapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.record.TourContext;
import org.example.tournamentapp.service.*;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/nextTour")
@RequiredArgsConstructor
@Slf4j
public class TourController {

    private final MergePairingService mergePairingService;
    private final TournamentProcessingService tournamentProcessingService;
    private final TournamentFinishingService tournamentFinishingService;
    private final TournamentDataService tournamentDataService;
    private final TournamentOpponentHistoryService tournamentOpponentHistoryService;

    @GetMapping
    public String showTourPage(Model model,
                               @RequestParam Long tournamentId,
                               RedirectAttributes redirectAttributes) {
        if (tournamentFinishingService.isEnded(tournamentId)) {
            redirectAttributes.addAttribute("tournamentId", tournamentId);
            return "redirect:/finalPage";
        }

        int currentTour = tournamentDataService.getCurrentTourByTourId(tournamentId);
        Map<Long, Set<String>> opponentsMap =  tournamentOpponentHistoryService.getOpponentsMap(tournamentId);
        List<PairDto> pairs = mergePairingService.getPairingList(opponentsMap,tournamentId,currentTour);

        PairsWrapper wrapper = new PairsWrapper(pairs);

        model.addAttribute("pairsWrapper", wrapper);
        model.addAttribute("currentTour", currentTour);
        model.addAttribute("tournamentId", tournamentId);

        return "nextTourPage";
    }

    @PostMapping()
    public String calculateScores(@ModelAttribute PairsWrapper pairsWrapper,
                                  Model model,
                                  @RequestParam Long tournamentId,
                                  RedirectAttributes redirectAttributes) {
        log.info("POST /nextTour. Calculate score requested for Tournament {}", tournamentId);
        TourContext tourContext = tournamentProcessingService.processingTournament(tournamentId, pairsWrapper);

        if (tourContext.ended()) {
            redirectAttributes.addAttribute("tournamentId", tournamentId);
            return "redirect:/finalPage";
        }

        model.addAttribute("pairsWrapper", new PairsWrapper(tourContext.newPairs()));
        model.addAttribute("currentTour", tourContext.updatedTour());

        redirectAttributes.addAttribute("tournamentId", tournamentId);
        return "redirect:/subTotalResult";
    }
}
