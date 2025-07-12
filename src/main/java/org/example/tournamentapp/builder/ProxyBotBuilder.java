package org.example.tournamentapp.builder;

import org.example.tournamentapp.model.Player;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProxyBotBuilder {

    public Player getProxyBot() {
        return Player.builder()
                .name("Proxy Bot")
                .faction("ProxyBot faction")
                .tp(0)
                .vp(0)
                .ap(0)
                .mp(0)
                .namesPlayed(new ArrayList<>())
                .build();
    }
}
