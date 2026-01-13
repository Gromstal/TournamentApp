package org.example.tournamentapp.exception;

import org.example.tournamentapp.exception.exceptionHandler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;
    @Mock
    private Model model;

    @Test
    void tournamentNotFoundTest() {
        TournamentNotFoundException ex = new TournamentNotFoundException("Tournament not found");

        String result = handler.tournamentNotFound(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }

    @Test
    void playerNotFoundTest() {
        PlayerNotFoundException ex = new PlayerNotFoundException("Player not found");

        String result = handler.playerNotFound(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }

    @Test
    void duplicatePlayerTest() {
        DuplicatePlayerException ex = new DuplicatePlayerException("Duplicate player");

        String result = handler.duplicatePlayer(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }

    @Test
    void typeMismatchTest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);

        String result = handler.methodArgumentTypeMismatch(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }

    @Test
    void missingParamTest() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("tournamentId", "Long");

        String result = handler.missingServletRequest(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }

    @Test
    void dataIntegrityTest() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");

        String result = handler.dataIntegrityViolation(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }

    @Test
    void runtimeExceptionTest() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        String result = handler.runTime(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }

    @Test
    void nullPointerTest() {
        NullPointerException ex = new NullPointerException("NPE");

        String result = handler.nullPointer(ex, model);

        assertEquals("errorPage", result);
        verify(model, times(1)).addAttribute(eq("message"), anyString());
    }
}


