package org.example.tournamentapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.service.TourService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping ("/finalPage")
@RequiredArgsConstructor
public class FinalPageController {

    private final TourService tourService;

    @GetMapping
    public String finalPage(Model model, HttpSession session) {
        PlayerListWrapper wrapper = new PlayerListWrapper();
        wrapper.setPlayerList(tourService.getFinalSortedList((List)session.getAttribute("playerList")));
        model.addAttribute("wrapper", wrapper);
        return "finalPage";
    }
}
