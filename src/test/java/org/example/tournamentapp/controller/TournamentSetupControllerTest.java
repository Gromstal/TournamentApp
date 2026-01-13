package org.example.tournamentapp.controller;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.ManualSetupOption;
import org.example.tournamentapp.service.SetupService;
import org.example.tournamentapp.service.TournamentCreatingService;
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

@WebMvcTest(TournamentSetupController.class)
class TournamentSetupControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TournamentCreatingService tournamentCreatingService;
    @MockBean
    private SetupService setupService;

    @Test
    void setupPageTest() throws Exception {
        when(setupService.setupPlayerList()).thenReturn(new PlayerListWrapper());

        mockMvc.perform(get("/setup"))
                .andExpect(status().isOk())
                .andExpect(view().name("setupPage"))
                .andExpect(model().attributeExists("wrapper"));
    }

    @Test
    void autoSetupTest() throws Exception {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("Player1").faction("F1").build(),
                PlayerDto.builder().name("Player2").faction("F2").build()
        );

        ManualSetupOption option = new ManualSetupOption(false, 100L);

        when(tournamentCreatingService.create(anyList(), eq(5), eq(false))).thenReturn(option);

        mockMvc.perform(post("/setup")
                        .param("tourCount", "5")
                        .param("tourFlag", "false")
                        .flashAttr("wrapper", new PlayerListWrapper(players)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nextTour?tournamentId=100"));
    }

    @Test
    void manualSetupTest() throws Exception {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("Player1").faction("F1").build(),
                PlayerDto.builder().name("Player2").faction("F2").build()
        );

        ManualSetupOption option = new ManualSetupOption(true, 200L);

        when(tournamentCreatingService.create(anyList(), eq(3), eq(true))).thenReturn(option);

        mockMvc.perform(post("/setup")
                        .param("tourCount", "3")
                        .param("tourFlag", "true")
                        .flashAttr("wrapper", new PlayerListWrapper(players)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hsetup?tournamentId=200"));
    }

    @Test
    void setupValidationErrorTest() throws Exception {
        PlayerListWrapper wrapper = new PlayerListWrapper(List.of(
                PlayerDto.builder().name("").faction("F1").build()
        ));

        mockMvc.perform(post("/setup")
                        .param("tourCount", "5")
                        .param("tourFlag", "false")
                        .flashAttr("wrapper", wrapper))
                .andExpect(status().isOk())
                .andExpect(view().name("setupPage"));

        verify(tournamentCreatingService, never()).create(anyList(), anyInt(), anyBoolean());
    }
}


