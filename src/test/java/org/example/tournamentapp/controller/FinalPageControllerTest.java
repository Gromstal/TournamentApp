package org.example.tournamentapp.controller;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.service.SortingPlayerService;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.service.TournamentFinishingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinalPageController.class)
@TestPropertySource(properties = "tournament.needToDelete=false")
class FinalPageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SortingPlayerService sortingPlayerService;
    @MockBean
    private TournamentFinishingService tournamentFinishingService;
    @MockBean
    private TournamentDataService tournamentDataService;

    @Test
    void finalPagePlayersTest() throws Exception {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("Player1").build(),
                PlayerDto.builder().name("Player2").build()
        );

        when(tournamentDataService.getPlayersDTO(1L)).thenReturn(players);
        when(sortingPlayerService.getSortedPlayerList(players)).thenReturn(players);

        mockMvc.perform(get("/finalPage")
                        .param("tournamentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("finalPage"))
                .andExpect(model().attributeExists("wrapper"))
                .andExpect(model().attribute("tournamentId", 1L));
    }

    @Test
    void finalPageEmptyTest() throws Exception {
        when(tournamentDataService.getPlayersDTO(99L)).thenReturn(List.of());
        when(sortingPlayerService.getSortedPlayerList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/finalPage")
                        .param("tournamentId", "99"))
                .andExpect(status().isOk())
                .andExpect(view().name("finalPage"));
    }

    @Test
    void finalPageNoDeleteTest() throws Exception {
        mockMvc.perform(post("/finalPage")
                        .param("tournamentId", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(tournamentFinishingService, never()).deleteTournament(anyLong());
    }
}


