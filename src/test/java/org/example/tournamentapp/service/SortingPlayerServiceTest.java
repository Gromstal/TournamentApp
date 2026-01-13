package org.example.tournamentapp.service;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.testData.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SortingPlayerServiceTest {

    @InjectMocks
    private SortingPlayerService sortingPlayerService;
    private final TestData testData = new TestData();

    @Test
    void sortingTest() {
        List<PlayerDto> sorted = sortingPlayerService.getSortedPlayerList(testData.getFinalResultList());

        assertThat(sorted)
                .extracting(PlayerDto::getName)
                .containsExactly(
                        "Паша",
                        "Витя",
                        "Даня",
                        "Настя",
                        "Михаил",
                        "Тигран",
                        "Иван",
                        "Евгений",
                        "Андрей П",
                        "Андрей Н"
                );
    }

    @Test
    void sortEmptyTest() {
        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(new ArrayList<>());

        assertThat(result).isEmpty();
    }

    @Test
    void sortSingleTest() {
        PlayerDto player = PlayerDto.builder()
                .name("Solo")
                .vp(10)
                .tp(5)
                .vpOpp(10)
                .totalAp(5)
                .totalMp(5)
                .build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(player));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Solo");
    }

    @Test
    void sortByVpTest() {
        PlayerDto low = PlayerDto.builder().name("Low").vp(5).tp(0).vpOpp(0).totalAp(0).totalMp(0).build();
        PlayerDto high = PlayerDto.builder().name("High").vp(15).tp(0).vpOpp(0).totalAp(0).totalMp(0).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(low, high));

        assertThat(result)
                .extracting(PlayerDto::getName)
                .containsExactly("High", "Low");
    }

    @Test
    void sortByTpTest() {
        PlayerDto lowTp = PlayerDto.builder().name("LowTp").vp(10).tp(5).vpOpp(0).totalAp(0).totalMp(0).build();
        PlayerDto highTp = PlayerDto.builder().name("HighTp").vp(10).tp(10).vpOpp(0).totalAp(0).totalMp(0).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(lowTp, highTp));

        assertThat(result)
                .extracting(PlayerDto::getName)
                .containsExactly("HighTp", "LowTp");
    }

    @Test
    void sortByVpOppTest() {
        PlayerDto lowOpp = PlayerDto.builder().name("LowOpp").vp(10).tp(5).vpOpp(10).totalAp(0).totalMp(0).build();
        PlayerDto highOpp = PlayerDto.builder().name("HighOpp").vp(10).tp(5).vpOpp(20).totalAp(0).totalMp(0).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(lowOpp, highOpp));

        assertThat(result)
                .extracting(PlayerDto::getName)
                .containsExactly("LowOpp", "HighOpp");
    }

    @Test
    void sortByApTest() {
        PlayerDto lowAp = PlayerDto.builder().name("LowAp").vp(10).tp(5).vpOpp(10).totalAp(5).totalMp(0).build();
        PlayerDto highAp = PlayerDto.builder().name("HighAp").vp(10).tp(5).vpOpp(10).totalAp(15).totalMp(0).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(lowAp, highAp));

        assertThat(result)
                .extracting(PlayerDto::getName)
                .containsExactly("HighAp", "LowAp");
    }

    @Test
    void sortByMpTest() {
        PlayerDto lowMp = PlayerDto.builder().name("LowMp").vp(10).tp(5).vpOpp(10).totalAp(10).totalMp(5).build();
        PlayerDto highMp = PlayerDto.builder().name("HighMp").vp(10).tp(5).vpOpp(10).totalAp(10).totalMp(15).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(lowMp, highMp));

        assertThat(result)
                .extracting(PlayerDto::getName)
                .containsExactly("HighMp", "LowMp");
    }

    @Test
    void sortComplexTest() {
        PlayerDto p1 = PlayerDto.builder().name("P1").vp(20).tp(10).vpOpp(30).totalAp(15).totalMp(10).build();
        PlayerDto p2 = PlayerDto.builder().name("P2").vp(20).tp(10).vpOpp(30).totalAp(15).totalMp(12).build();
        PlayerDto p3 = PlayerDto.builder().name("P3").vp(20).tp(10).vpOpp(30).totalAp(20).totalMp(10).build();
        PlayerDto p4 = PlayerDto.builder().name("P4").vp(20).tp(12).vpOpp(30).totalAp(10).totalMp(10).build();
        PlayerDto p5 = PlayerDto.builder().name("P5").vp(25).tp(8).vpOpp(20).totalAp(5).totalMp(5).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(p1, p2, p3, p4, p5));

        assertThat(result)
                .extracting(PlayerDto::getName)
                .containsExactly("P4", "P2", "P3", "P1", "P5");
    }

    @Test
    void sortSameStatsTest() {
        PlayerDto p1 = PlayerDto.builder().name("A").vp(10).tp(5).vpOpp(10).totalAp(5).totalMp(5).build();
        PlayerDto p2 = PlayerDto.builder().name("B").vp(10).tp(5).vpOpp(10).totalAp(5).totalMp(5).build();
        PlayerDto p3 = PlayerDto.builder().name("C").vp(10).tp(5).vpOpp(10).totalAp(5).totalMp(5).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(p1, p2, p3));

        assertThat(result).hasSize(3);
    }

    @Test
    void sortNegativeTest() {
        PlayerDto negative = PlayerDto.builder().name("Negative").vp(-5).tp(-2).vpOpp(0).totalAp(0).totalMp(0).build();
        PlayerDto positive = PlayerDto.builder().name("Positive").vp(5).tp(2).vpOpp(0).totalAp(0).totalMp(0).build();

        List<PlayerDto> result = sortingPlayerService.getSortedPlayerList(List.of(negative, positive));

        assertThat(result)
                .extracting(PlayerDto::getName)
                .containsExactly("Positive", "Negative");
    }
}


