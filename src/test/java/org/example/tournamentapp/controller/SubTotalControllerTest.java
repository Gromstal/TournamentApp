package org.example.tournamentapp.controller;

import org.example.tournamentapp.model.record.TournamentSubTotalOption;
import org.example.tournamentapp.service.TournamentSubTotalService;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubTotalController.class)
class SubTotalControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TournamentSubTotalService tournamentSubTotalService;

    @Test
    void subTotalPageTest() throws Exception {
        TournamentSubTotalOption option = new TournamentSubTotalOption(
                3,
                new PlayerListWrapper(List.of())
        );

        when(tournamentSubTotalService.getTournamentSubTotalOption(1L)).thenReturn(option);

        mockMvc.perform(get("/subTotalResult")
                        .param("tournamentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("subTotalPage"))
                .andExpect(model().attributeExists("wrapper"))
                .andExpect(model().attribute("currentTour", 3))
                .andExpect(model().attribute("tournamentId", 1L));

        verify(tournamentSubTotalService, times(1)).getTournamentSubTotalOption(1L);
    }

    @Test
    void subTotalFirstTourTest() throws Exception {
        TournamentSubTotalOption option = new TournamentSubTotalOption(1, new PlayerListWrapper(List.of()));

        when(tournamentSubTotalService.getTournamentSubTotalOption(5L)).thenReturn(option);

        mockMvc.perform(get("/subTotalResult")
                        .param("tournamentId", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentTour", 1));
    }

    @Test
    void subTotalDifferentIdTest() throws Exception {
        TournamentSubTotalOption option = new TournamentSubTotalOption(2, new PlayerListWrapper(List.of()));

        when(tournamentSubTotalService.getTournamentSubTotalOption(99L)).thenReturn(option);

        mockMvc.perform(get("/subTotalResult")
                        .param("tournamentId", "99"))
                .andExpect(status().isOk())
                .andExpect(view().name("subTotalPage"));
    }
}


