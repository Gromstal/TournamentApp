package org.example.tournamentapp.builder;

import org.example.tournamentapp.model.PlayerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProxyBotBuilderTest {

    @InjectMocks
    private ProxyBotBuilder builder;

    @Test
    void proxyBotTest() {
        PlayerDto bot = builder.getProxyBot();

        assertThat(bot).isNotNull();
        assertThat(bot.getName()).isEqualTo("Proxy Bot");
        assertThat(bot.getFaction()).isEqualTo("ProxyBot faction");
    }

    @Test
    void proxyBotConsistentTest() {
        PlayerDto bot1 = builder.getProxyBot();
        PlayerDto bot2 = builder.getProxyBot();

        assertThat(bot1.getName()).isEqualTo(bot2.getName());
        assertThat(bot1.getFaction()).isEqualTo(bot2.getFaction());
    }
}
