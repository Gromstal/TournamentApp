package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.model.PlayerDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProxyBotService {

    private final ProxyBotBuilder proxyBotBuilder;

    public List<PlayerDto> getPlayerListWithProxyBot(List<PlayerDto> setupList) {
        if (setupList.size() < 2) {
            log.info("Not enough players. No ProxyBot");
            return setupList;
        }

        if (setupList.size() % 2 != 0) {
            setupList.add(proxyBotBuilder.getProxyBot());
        }
        log.info("ProxyBot added");
        return setupList;
    }
}