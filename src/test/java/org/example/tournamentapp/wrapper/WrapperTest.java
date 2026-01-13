package org.example.tournamentapp.wrapper;

import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WrapperTest {

    @Test
    void playerWrapperDefaultTest() {
        PlayerListWrapper wrapper = new PlayerListWrapper();

        assertThat(wrapper).isNotNull();
    }

    @Test
    void playerWrapperListTest() {
        List<PlayerDto> players = List.of(
                PlayerDto.builder().name("P1").build(),
                PlayerDto.builder().name("P2").build()
        );

        PlayerListWrapper wrapper = new PlayerListWrapper(players);

        assertThat(wrapper.getPlayerList()).hasSize(2);
    }

    @Test
    void playerWrapperSetGetTest() {
        PlayerListWrapper wrapper = new PlayerListWrapper();
        List<PlayerDto> players = new ArrayList<>();
        players.add(PlayerDto.builder().name("Player1").build());

        wrapper.setPlayerList(players);

        assertThat(wrapper.getPlayerList()).hasSize(1);
        assertThat(wrapper.getPlayerList().get(0).getName()).isEqualTo("Player1");
    }

    @Test
    void pairsWrapperDefaultTest() {
        PairsWrapper wrapper = new PairsWrapper();

        assertThat(wrapper).isNotNull();
    }

    @Test
    void pairsWrapperListTest() {
        List<PairDto> pairs = List.of(new PairDto());

        PairsWrapper wrapper = new PairsWrapper(pairs);

        assertThat(wrapper.getPairs()).hasSize(1);
    }

    @Test
    void pairsWrapperSetGetTest() {
        PairsWrapper wrapper = new PairsWrapper();
        List<PairDto> pairs = new ArrayList<>();
        pairs.add(new PairDto());

        wrapper.setPairs(pairs);

        assertThat(wrapper.getPairs()).hasSize(1);
    }

    @Test
    void playerWrapperEmptyTest() {
        PlayerListWrapper wrapper = new PlayerListWrapper(new ArrayList<>());

        assertThat(wrapper.getPlayerList()).isEmpty();
    }

    @Test
    void pairsWrapperEmptyTest() {
        PairsWrapper wrapper = new PairsWrapper(new ArrayList<>());

        assertThat(wrapper.getPairs()).isEmpty();
    }
}


