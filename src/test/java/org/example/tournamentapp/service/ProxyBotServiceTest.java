package org.example.tournamentapp.service;

import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.model.PlayerDto;
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
class ProxyBotServiceTest {

    @Mock
    private ProxyBotBuilder proxyBotBuilder;
    @InjectMocks
    private ProxyBotService service;

    @Test
    void emptyListTest() {
        List<PlayerDto> input = new ArrayList<>();

        List<PlayerDto> result = service.getPlayerListWithProxyBot(input);

        assertEquals(0, result.size());
        verify(proxyBotBuilder, never()).getProxyBot();
    }

    @Test
    void singlePlayerTest() {
        List<PlayerDto> input = new ArrayList<>();
        input.add(PlayerDto.builder().name("Player1").build());

        List<PlayerDto> result = service.getPlayerListWithProxyBot(input);

        assertEquals(1, result.size());
        verify(proxyBotBuilder, never()).getProxyBot();
    }

    @Test
    void evenCountTest() {
        List<PlayerDto> input = new ArrayList<>();
        input.add(PlayerDto.builder().name("Player1").build());
        input.add(PlayerDto.builder().name("Player2").build());

        List<PlayerDto> result = service.getPlayerListWithProxyBot(input);

        assertEquals(2, result.size());
        verify(proxyBotBuilder, never()).getProxyBot();
    }

    @Test
    void oddCountTest() {
        List<PlayerDto> input = new ArrayList<>();
        input.add(PlayerDto.builder().name("Player1").build());
        input.add(PlayerDto.builder().name("Player2").build());
        input.add(PlayerDto.builder().name("Player3").build());

        PlayerDto proxyBot = PlayerDto.builder().name("ProxyBot").build();
        when(proxyBotBuilder.getProxyBot()).thenReturn(proxyBot);

        List<PlayerDto> result = service.getPlayerListWithProxyBot(input);

        assertEquals(4, result.size());
        assertTrue(result.contains(proxyBot));
        verify(proxyBotBuilder, times(1)).getProxyBot();
    }

    @Test
    void largeOddCountTest() {
        List<PlayerDto> input = new ArrayList<>();
        for (int i = 1; i <= 99; i++) {
            input.add(PlayerDto.builder().name("Player" + i).build());
        }

        PlayerDto proxyBot = PlayerDto.builder().name("ProxyBot").build();
        when(proxyBotBuilder.getProxyBot()).thenReturn(proxyBot);

        List<PlayerDto> result = service.getPlayerListWithProxyBot(input);

        assertEquals(100, result.size());
        assertTrue(result.contains(proxyBot));
        verify(proxyBotBuilder, times(1)).getProxyBot();
    }

    @Test
    void largeEvenCountTest() {
        List<PlayerDto> input = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            input.add(PlayerDto.builder().name("Player" + i).build());
        }

        List<PlayerDto> result = service.getPlayerListWithProxyBot(input);

        assertEquals(100, result.size());
        verify(proxyBotBuilder, never()).getProxyBot();
    }
}
