package org.example.tournamentapp.controller;

import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.model.record.ManualPairsSetupOption;
import org.example.tournamentapp.service.CreatingPairingService;
import org.example.tournamentapp.service.ManualSetupService;
import org.example.tournamentapp.service.TournamentDataService;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManualSetupController.class)
class ManualSetupControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CreatingPairingService creatingPairingService;
    @MockBean
    private ManualSetupService manualSetupService;
    @MockBean
    private TournamentDataService tournamentDataService;

    @Test
    void manualSetupPageTest() throws Exception {
        PlayerDto p1 = PlayerDto.builder().id(1L).name("Player1").build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("Player2").build();
        List<PlayerDto> players = List.of(p1, p2);

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(new PairDto()));

        ManualPairsSetupOption option = new ManualPairsSetupOption(players, wrapper);

        when(manualSetupService.getSetupPairWrapper(100L)).thenReturn(option);

        mockMvc.perform(get("/hsetup").param("tournamentId", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("handSetupPage"))
                .andExpect(model().attributeExists("pairsWrapper"))
                .andExpect(model().attributeExists("playerList"))
                .andExpect(model().attribute("tournamentId", 100L));

        verify(manualSetupService, times(1)).getSetupPairWrapper(100L);
    }

    @Test
    void manualPairingSaveTest() throws Exception {
        PlayerDto p1 = PlayerDto.builder().id(1L).name("Player1").namesPlayed(new HashSet<>()).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("Player2").namesPlayed(new HashSet<>()).build();

        PairDto pair = new PairDto(p1, p2);
        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(pair));

        mockMvc.perform(post("/hsetup")
                        .param("tournamentId", "100")
                        .param("pairs[0].firstPlayer.id", "1")
                        .param("pairs[0].secondPlayer.id", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nextTour?tournamentId=100"));

        verify(manualSetupService, times(1)).saveManualSetup(eq(100L), anyList());
    }

    @Test
    void manualPairingMultipleTest() throws Exception {
        PlayerDto p1 = PlayerDto.builder().id(1L).name("Player1").namesPlayed(new HashSet<>()).build();
        PlayerDto p2 = PlayerDto.builder().id(2L).name("Player2").namesPlayed(new HashSet<>()).build();
        PlayerDto p3 = PlayerDto.builder().id(3L).name("Player3").namesPlayed(new HashSet<>()).build();
        PlayerDto p4 = PlayerDto.builder().id(4L).name("Player4").namesPlayed(new HashSet<>()).build();

        mockMvc.perform(post("/hsetup")
                        .param("tournamentId", "100")
                        .param("pairs[0].firstPlayer.id", "1")
                        .param("pairs[0].secondPlayer.id", "2")
                        .param("pairs[1].firstPlayer.id", "3")
                        .param("pairs[1].secondPlayer.id", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nextTour?tournamentId=100"));

        verify(manualSetupService, times(1)).saveManualSetup(eq(100L), anyList());
    }

    @Test
    void manualSetupDifferentIdTest() throws Exception {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().id(10L).name("Player10").build()
        );

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(new PairDto()));

        ManualPairsSetupOption option = new ManualPairsSetupOption(players, wrapper);

        when(manualSetupService.getSetupPairWrapper(999L)).thenReturn(option);

        mockMvc.perform(get("/hsetup").param("tournamentId", "999"))
                .andExpect(status().isOk())
                .andExpect(view().name("handSetupPage"))
                .andExpect(model().attribute("tournamentId", 999L));
    }
}


