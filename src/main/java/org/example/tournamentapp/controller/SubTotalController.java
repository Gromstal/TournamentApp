package org.example.tournamentapp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.service.SortingPlayerService;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/subTotalResult")
@RequiredArgsConstructor
@Slf4j
public class SubTotalController {

    private final SortingPlayerService sortingPlayerService;
    private final TournamentDataService tournamentDataService;

    @GetMapping
    public String getSubTotalPage(Model model,
                                  @RequestParam Long tournamentId) {
        List<PlayerDto> playerDtoList = tournamentDataService.getPlayersDTO(tournamentId);
        int currentTour = tournamentDataService.getCurrentTourByTourId(tournamentId);

        PlayerListWrapper wrapper = new PlayerListWrapper();
        wrapper.setPlayerList(sortingPlayerService.getSortedPlayerList(playerDtoList));

        model.addAttribute("wrapper", wrapper);
        model.addAttribute("currentTour", currentTour);
        model.addAttribute("tournamentId", tournamentId);

        log.info("GET /subTotalResult. Getting result for tour {} Tournament {} ", currentTour, tournamentId);
        return "subTotalPage";
    }

}