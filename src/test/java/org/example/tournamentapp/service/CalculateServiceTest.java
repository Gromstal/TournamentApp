package org.example.tournamentapp.service;

import org.example.tournamentapp.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateServiceTest {

    private CalculateService service;

    @BeforeEach
    void setUp() {
        service = new CalculateService();
    }

    @Test
    void testTp4() {
        Player first = player(0, 10, 10);
        Player second = player(0, 5, 5);

        service.calculate(first, second);

        assertThat(first.getTp()).isEqualTo(4);
        assertThat(second.getTp()).isEqualTo(0);
        assertTotalsUpdated(first);
        assertTotalsUpdated(second);
    }

    @Test
    void testTp3_VPGreaterButOneLess() {
        Player first = player(0, 4, 9);  // VP >,  AP <
        Player second = player(0, 5, 7);

        service.calculate(first, second);

        assertThat(first.getTp()).isEqualTo(3);
        assertThat(second.getTp()).isEqualTo(1);
        assertTotalsUpdated(first);
        assertTotalsUpdated(second);
    }

    @Test
    void testTp3_VPGreaterButOneEqual() {
        Player first = player(0, 5, 9);  // VP >, MP =
        Player second = player(0, 5, 7);

        service.calculate(first, second);

        assertThat(first.getTp()).isEqualTo(3);
        assertThat(second.getTp()).isEqualTo(1);
        assertTotalsUpdated(first);
        assertTotalsUpdated(second);
    }

    @Test
    void testTp2() {
        Player first = player(0, 5, 5);
        Player second = player(0, 5, 5);

        service.calculate(first, second);

        assertThat(first.getTp()).isEqualTo(2);
        assertThat(second.getTp()).isEqualTo(2);
        assertTotalsUpdated(first);
        assertTotalsUpdated(second);
    }

    @Test
    void testTp22() {
        Player first = player(0, 4, 5);
        Player second = player(0, 5, 4);

        service.calculate(first, second);

        assertThat(first.getTp()).isEqualTo(2);
        assertThat(second.getTp()).isEqualTo(2);
        assertTotalsUpdated(first);
        assertTotalsUpdated(second);
    }

    @Test
    void testTp0() {
        Player first = player(5, 5, 5);
        Player second = player(10, 10, 10);

        service.calculate(first, second);

        assertThat(first.getTp()).isEqualTo(0);
        assertThat(second.getTp()).isEqualTo(4);
        assertTotalsUpdated(first);
        assertTotalsUpdated(second);
    }

    private Player player(int vp, int mp, int ap) {
        return Player.builder()
                .name("TestPlayer")
                .faction("TestFaction")
                .vp(vp)
                .mp(mp)
                .ap(ap)
                .tp(0)
                .totalAp(0)
                .totalMp(0)
                .inPair(false)
                .build();
    }

    private void assertTotalsUpdated(Player player) {
        assertThat(player.getTotalAp()).isEqualTo(player.getAp());
        assertThat(player.getTotalMp()).isEqualTo(player.getMp());
    }

}