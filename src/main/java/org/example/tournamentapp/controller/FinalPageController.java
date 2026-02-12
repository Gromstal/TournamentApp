package org.example.tournamentapp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.service.SortingPlayerService;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.service.TournamentFinishingService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/finalPage")
@RequiredArgsConstructor
@Slf4j
public class FinalPageController {

    private final SortingPlayerService sortingPlayerService;
    private final TournamentFinishingService tournamentFinishingService;
    private final TournamentDataService tournamentDataService;
    @Value("${tournament.needToDelete}")
    private boolean needToDelete;

    @GetMapping
    public String finalPage(Model model,
                            @RequestParam Long tournamentId) {
        PlayerListWrapper wrapper = new PlayerListWrapper();
        wrapper.setPlayerList(sortingPlayerService.getSortedPlayerList(tournamentDataService.getPlayersDTO(tournamentId)));
        model.addAttribute("wrapper", wrapper);
        model.addAttribute("tournamentId", tournamentId);

        return "finalPage";
    }

    @PostMapping
    public String finalPageSubmit(@RequestParam Long tournamentId) {
        log.info("POST /finalPage. Finish/Delete tournament requested for Tournament {}, redirecting to starting page", tournamentId);
        log.info("Config needToDelete: {} ", needToDelete);
        if (needToDelete) tournamentFinishingService.deleteTournament(tournamentId);

        return "redirect:/";
    }
}