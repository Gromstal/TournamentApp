package org.example.tournamentapp.controller;

import org.example.tournamentapp.service.SortingPlayerService;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.service.TournamentFinishingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinalPageController.class)
@TestPropertySource(properties = "tournament.needToDelete=true")
class FinalPageControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SortingPlayerService sortingPlayerService;
    @MockBean
    private TournamentFinishingService tournamentFinishingService;
    @MockBean
    private TournamentDataService tournamentDataService;

    @Test
    void finalPageDeleteTest() throws Exception {
        mockMvc.perform(post("/finalPage")
                        .param("tournamentId", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(tournamentFinishingService, times(1)).deleteTournament(5L);
    }
}
