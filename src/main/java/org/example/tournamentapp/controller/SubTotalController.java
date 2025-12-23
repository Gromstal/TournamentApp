package org.example.tournamentapp.controller;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.service.SortingPlayerService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/subTotalResult")
@RequiredArgsConstructor
public class SubTotalController {

    private final SortingPlayerService sortingPlayerService;

    @GetMapping
    public String getSubTotalPage(Model model, HttpSession session) {
        PlayerListWrapper wrapper = new PlayerListWrapper();

        List<PlayerDto> playerDtos = (List<PlayerDto>) session.getAttribute("players");
        wrapper.setPlayerList(sortingPlayerService.getSortedPlayerList(playerDtos));

        model.addAttribute("wrapper", wrapper);
        model.addAttribute("currentTour", session.getAttribute("currentTour"));
        return "subTotalPage";
    }

}