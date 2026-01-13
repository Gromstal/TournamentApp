package org.example.tournamentapp.controller;

import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.ProcessingOption;
import org.example.tournamentapp.model.record.TourContext;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.service.TournamentFinishingService;
import org.example.tournamentapp.service.TournamentProcessingService;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourController.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TournamentProcessingService tournamentProcessingService;
    @MockBean
    private TournamentFinishingService tournamentFinishingService;
    @MockBean
    private TournamentDataService tournamentDataService;

    @Test
    void tourPageNotEndedTest() throws Exception {
        PlayerDto p1 = PlayerDto.builder().id(1L).name("P1").mp(0).ap(0).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("P2").mp(0).ap(0).build();
        PairDto pair = new PairDto(p1, p2);
        ProcessingOption option = new ProcessingOption(2, new PairsWrapper(List.of(pair)));

        when(tournamentFinishingService.isEnded(1L)).thenReturn(false);
        when(tournamentProcessingService.getProcessingOption(1L)).thenReturn(option);

        mockMvc.perform(get("/nextTour")
                        .param("tournamentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("nextTourPage"))
                .andExpect(model().attributeExists("pairsWrapper"))
                .andExpect(model().attribute("currentTour", 2))
                .andExpect(model().attribute("tournamentId", 1L));
    }

    @Test
    void tourPageEndedTest() throws Exception {
        when(tournamentFinishingService.isEnded(1L)).thenReturn(true);

        mockMvc.perform(get("/nextTour")
                        .param("tournamentId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/finalPage?tournamentId=1"));

        verify(tournamentProcessingService, never()).getProcessingOption(anyLong());
    }

    @Test
    void scoresNextTourTest() throws Exception {
        TourContext context = new TourContext(false, 3, List.of(), List.of());

        when(tournamentProcessingService.processingTournament(eq(1L), any(PairsWrapper.class))).thenReturn(context);

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", "1")
                        .flashAttr("pairsWrapper", new PairsWrapper(List.of())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/subTotalResult?tournamentId=1"));
    }

    @Test
    void scoresFinalTourTest() throws Exception {
        TourContext context = new TourContext(true, 5, List.of(), List.of());

        when(tournamentProcessingService.processingTournament(eq(1L), any(PairsWrapper.class))).thenReturn(context);

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", "1")
                        .flashAttr("pairsWrapper", new PairsWrapper(List.of())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/finalPage?tournamentId=1"));
    }

    @Test
    void scoresProcessTest() throws Exception {
        TourContext context = new TourContext(false, 2, List.of(), List.of());

        when(tournamentProcessingService.processingTournament(eq(1L), any(PairsWrapper.class))).thenReturn(context);

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", "1")
                        .flashAttr("pairsWrapper", new PairsWrapper(List.of(new PairDto()))))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void scoresValidationErrorTest() throws Exception {
        PlayerDto p1 = PlayerDto.builder().id(1L).name("P1").mp(null).ap(null).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("P2").mp(null).ap(null).build();
        PairDto pair = new PairDto(p1, p2);

        when(tournamentDataService.getCurrentTourByTournamentId(1L)).thenReturn(3);

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", "1")
                        .param("pairs[0].firstPlayer.id", "1")
                        .param("pairs[0].secondPlayer.id", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("nextTourPage"))
                .andExpect(model().attribute("currentTour", 3))
                .andExpect(model().attribute("tournamentId", 1L));

        verify(tournamentProcessingService, never()).processingTournament(anyLong(), any());
    }

    @Test
    void scoresInvalidApTest() throws Exception {
        when(tournamentDataService.getCurrentTourByTournamentId(1L)).thenReturn(2);

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", "1")
                        .param("pairs[0].firstPlayer.id", "1")
                        .param("pairs[0].firstPlayer.mp", "10")
                        .param("pairs[0].firstPlayer.ap", "-1")
                        .param("pairs[0].secondPlayer.id", "2")
                        .param("pairs[0].secondPlayer.mp", "10")
                        .param("pairs[0].secondPlayer.ap", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("nextTourPage"));

        verify(tournamentProcessingService, never()).processingTournament(anyLong(), any());
    }

    @Test
    void scoresInvalidMpTest() throws Exception {
        when(tournamentDataService.getCurrentTourByTournamentId(1L)).thenReturn(2);

        mockMvc.perform(post("/nextTour")
                        .param("tournamentId", "1")
                        .param("pairs[0].firstPlayer.id", "1")
                        .param("pairs[0].firstPlayer.mp", "1000")
                        .param("pairs[0].firstPlayer.ap", "10")
                        .param("pairs[0].secondPlayer.id", "2")
                        .param("pairs[0].secondPlayer.mp", "10")
                        .param("pairs[0].secondPlayer.ap", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("nextTourPage"));

        verify(tournamentProcessingService, never()).processingTournament(anyLong(), any());
    }
}


