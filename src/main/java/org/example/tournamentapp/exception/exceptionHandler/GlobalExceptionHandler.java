package org.example.tournamentapp.exception.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.exception.DuplicatePlayerException;
import org.example.tournamentapp.exception.InvalidPairingException;
import org.example.tournamentapp.exception.PlayerNotFoundException;
import org.example.tournamentapp.exception.TournamentNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TournamentNotFoundException.class)
    public String tournamentNotFound(TournamentNotFoundException ex, Model model) {
        model.addAttribute("message", "Турнир с указанным id не найден или его не существует");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public String playerNotFound(PlayerNotFoundException ex, Model model) {
        model.addAttribute("message", "По какой-то причине игрок не найден =(");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String methodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        model.addAttribute("message", "В адресной строке в параметре tournamentId= указано некорректное значение");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String missingServletRequest(MissingServletRequestParameterException ex, Model model) {
        model.addAttribute("message", "В адресной строке отсутствует значение параметра tournamentId=");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String dataIntegrityViolation(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("message", "Каким то образом появились дубли игроков. Лучше создать новый турнир без дублей");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(DuplicatePlayerException.class)
    public String duplicatePlayer(DuplicatePlayerException ex, Model model) {
        model.addAttribute("message", "Каким то образом появились дубли игроков. Лучше создать новый турнир без дублей");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(InvalidPairingException.class)
    public String invalidPairing(InvalidPairingException ex, Model model) {
        model.addAttribute("message", "Каким то образом появились дубли пар. Лучше создать новый турнир и попробовать заново");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(NullPointerException.class)
    public String nullPointer(NullPointerException ex, Model model) {
        model.addAttribute("message", "...и причина неизвестна");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

    @ExceptionHandler(RuntimeException.class)
    public String runTime(RuntimeException ex, Model model) {
        model.addAttribute("message", "...и причина неизвестна");
        log.error(ex.getMessage(), ex);
        return "errorPage";
    }

}
