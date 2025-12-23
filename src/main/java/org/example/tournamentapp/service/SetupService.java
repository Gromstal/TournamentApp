package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class SetupService {

    private final ProxyBotService proxyBotService;
    private final PlayerListWrapper playerListWrapper;

    public PlayerListWrapper setupPlayerList() {
        playerListWrapper.setPlayerList(new ArrayList<>());
        return playerListWrapper;
    }

    public List<PlayerDto> setupPlayerListWithPB(List<PlayerDto> setupList) {
        if (setupList.size() % 2 != 0) {
            setupList.add(proxyBotService.getProxyBot());
        }
        return setupList;
    }

}
