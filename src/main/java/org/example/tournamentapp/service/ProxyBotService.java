package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProxyBotService {

    private final ProxyBotBuilder proxyBotBuilder;

    public List<PlayerDto> getPlayerListWithProxyBot(List<PlayerDto> setupList) {
        if (setupList.size() % 2 != 0) {
            setupList.add(proxyBotBuilder.getProxyBot());
        }
        return setupList;
    }
}