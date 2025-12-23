package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProxyBotService {

    private final ProxyBotBuilder proxyBotBuilder;

    public PlayerDto getProxyBot() {
        return proxyBotBuilder.getProxyBot();
    }
}
