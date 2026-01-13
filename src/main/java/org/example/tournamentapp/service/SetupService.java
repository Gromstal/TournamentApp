package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class SetupService {

    private final ProxyBotService proxyBotService;

    public PlayerListWrapper setupPlayerList() {
        PlayerListWrapper playerListWrapper = new PlayerListWrapper();
        playerListWrapper.setPlayerList(new ArrayList<>());
        return playerListWrapper;
    }

    public List<PlayerDto> setupPlayerListWithProxyBot(List<PlayerDto> setupList) {
        return proxyBotService.getPlayerListWithProxyBot(setupList);
    }

}
