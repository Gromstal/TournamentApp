package org.example.tournamentapp.controller;

import org.example.tournamentapp.model.TournamentHistory;
import org.example.tournamentapp.service.TournamentHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StartingPageController.class)
class StartingPageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TournamentHistoryService tournamentHistoryService;

    @Test
    void startingPageViewTest() throws Exception {
        List<TournamentHistory> tournaments = List.of(
                new TournamentHistory(1L, java.time.LocalDate.of(2024, 1, 15)),
                new TournamentHistory(2L, java.time.LocalDate.of(2024, 2, 20))
        );

        when(tournamentHistoryService.getTournamentHistoryList()).thenReturn(tournaments);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("startingPage"))
                .andExpect(model().attributeExists("tournaments"))
                .andExpect(model().attribute("tournaments", tournaments));

        verify(tournamentHistoryService, times(1)).getTournamentHistoryList();
    }

    @Test
    void startingPageEmptyTest() throws Exception {
        when(tournamentHistoryService.getTournamentHistoryList()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("startingPage"));
    }

    @Test
    void startingPageSubmitTest() throws Exception {
        mockMvc.perform(post("/")
                        .param("tournamentId", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nextTour?tournamentId=5"));
    }

    @Test
    void startingPageResumeTest() throws Exception {
        mockMvc.perform(post("/")
                        .param("tournamentId", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nextTour?tournamentId=99"));
    }
}


