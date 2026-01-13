package org.example.tournamentapp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.record.TournamentSubTotalOption;
import org.example.tournamentapp.service.TournamentSubTotalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/subTotalResult")
@RequiredArgsConstructor
@Slf4j
public class SubTotalController {

    private final TournamentSubTotalService tournamentSubTotalService;

    @GetMapping
    public String getSubTotalPage(Model model,
                                  @RequestParam Long tournamentId) {

        TournamentSubTotalOption subTotalOption = tournamentSubTotalService.getTournamentSubTotalOption(tournamentId);

        model.addAttribute("wrapper", subTotalOption.wrapper());
        model.addAttribute("currentTour", subTotalOption.currentTour());
        model.addAttribute("tournamentId", tournamentId);

        log.info("GET /subTotalResult. Getting result for tour {} Tournament {} ", subTotalOption.currentTour(), tournamentId);
        return "subTotalPage";
    }

}