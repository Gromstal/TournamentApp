package org.example.tournamentapp.service;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetupServiceTest {

    @Mock
    private ProxyBotService proxyBotService;
    @InjectMocks
    private SetupService setupService;

    @Test
    void setupEmptyTest() {
        PlayerListWrapper result = setupService.setupPlayerList();

        assertNotNull(result);
        assertNotNull(result.getPlayerList());
        assertTrue(result.getPlayerList().isEmpty());
    }

    @Test
    void setupProxyBotTest() {
        List<PlayerDto> input = List.of(
                PlayerDto.builder().name("Player1").build(),
                PlayerDto.builder().name("Player2").build()
        );

        List<PlayerDto> expected = new ArrayList<>(input);
        expected.add(PlayerDto.builder().name("ProxyBot").build());

        when(proxyBotService.getPlayerListWithProxyBot(input)).thenReturn(expected);

        List<PlayerDto> result = setupService.setupPlayerListWithProxyBot(input);

        assertEquals(3, result.size());
        verify(proxyBotService, times(1)).getPlayerListWithProxyBot(input);
    }

    @Test
    void setupEvenCountTest() {
        List<PlayerDto> input = List.of(
                PlayerDto.builder().name("Player1").build(),
                PlayerDto.builder().name("Player2").build()
        );

        when(proxyBotService.getPlayerListWithProxyBot(input)).thenReturn(input);

        List<PlayerDto> result = setupService.setupPlayerListWithProxyBot(input);

        assertEquals(2, result.size());
        verify(proxyBotService, times(1)).getPlayerListWithProxyBot(input);
    }

    @Test
    void setupProxyEmptyTest() {
        List<PlayerDto> input = List.of();

        when(proxyBotService.getPlayerListWithProxyBot(input)).thenReturn(input);

        List<PlayerDto> result = setupService.setupPlayerListWithProxyBot(input);

        assertTrue(result.isEmpty());
    }
}


