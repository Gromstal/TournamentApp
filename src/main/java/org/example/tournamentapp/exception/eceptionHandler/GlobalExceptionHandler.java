package org.example.tournamentapp.exception.eceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.exception.PlayerNotFoundException;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TournamentNotFoundException.class)
    public String tournamentNotFound(TournamentNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return "error/404";
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public String playerNotFound(PlayerNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return "error/404";
    }
}
