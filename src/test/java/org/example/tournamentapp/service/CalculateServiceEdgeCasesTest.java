package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateServiceEdgeCasesTest {

    @InjectMocks
    private CalculateService service;
    @Mock
    private PlayerService playerService;

    @Test
    void zeroScoresTest() {
        PlayerDto firstDto = player(1L, 0, 0, 0);
        PlayerDto secondDto = player(2L, 0, 0, 0);

        PlayerEntity firstEntity = entityFromDb(1L, 0, 0);
        PlayerEntity secondEntity = entityFromDb(2L, 0, 0);

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(2);
        assertThat(secondEntity.getTp()).isEqualTo(2);
        assertThat(firstEntity.getVp()).isEqualTo(0);
        assertThat(secondEntity.getVp()).isEqualTo(0);
    }

    @Test
    void maxScoresTest() {
        PlayerDto firstDto = player(1L, 0, 20, 20);
        PlayerDto secondDto = player(2L, 0, 0, 0);

        PlayerEntity firstEntity = entityFromDb(1L, 0, 0);
        PlayerEntity secondEntity = entityFromDb(2L, 0, 0);

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(4);
        assertThat(secondEntity.getTp()).isEqualTo(0);
        assertThat(firstEntity.getVp()).isEqualTo(20 + 20);
        assertThat(secondEntity.getVp()).isEqualTo(0);
    }

    @Test
    void pairsWrapperTest() {
        PlayerDto p1 = player(1L, 0, 5, 5);
        PlayerDto p2 = player(2L, 0, 5, 5);
        PlayerDto p3 = player(3L, 0, 10, 10);
        PlayerDto p4 = player(4L, 0, 0, 0);

        List<PairDto> pairs = List.of(
                new PairDto(p1, p2),
                new PairDto(p3, p4)
        );

        when(playerService.getPlayerById(anyLong())).thenReturn(entityFromDb(1L, 0, 0));

        service.calculateFromPairsWrapper(1L, new PairsWrapper(pairs));

        verify(playerService, times(4)).getPlayerById(anyLong());
        verify(playerService, times(4)).savePlayer(any());
    }

    @Test
    void tpAccumulatesTest() {
        PlayerDto firstDto = player(1L, 10, 10, 0);
        PlayerDto secondDto = player(2L, 6, 0, 0);

        PlayerEntity firstEntity = entityFromDb(1L, 10, 10);
        PlayerEntity secondEntity = entityFromDb(2L, 6, 5);

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        int finalTp1 = firstEntity.getTp();
        int finalTp2 = secondEntity.getTp();
        assertThat(finalTp1 + finalTp2).isGreaterThan(0);
    }

    @Test
    void largeVpDiffTest() {
        PlayerDto firstDto = player(1L, 0, 20, 20);
        PlayerDto secondDto = player(2L, 0, 1, 0);

        PlayerEntity firstEntity = entityFromDb(1L, 0, 0);
        PlayerEntity secondEntity = entityFromDb(2L, 0, 0);

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(4);
        assertThat(secondEntity.getTp()).isEqualTo(0);
    }

    @Test
    void onlyMpTest() {
        PlayerDto firstDto = player(1L, 0, 10, 0);
        PlayerDto secondDto = player(2L, 0, 5, 0);

        PlayerEntity firstEntity = entityFromDb(1L, 0, 0);
        PlayerEntity secondEntity = entityFromDb(2L, 0, 0);

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isGreaterThanOrEqualTo(0);
        assertThat(firstEntity.getVp()).isEqualTo(10);
        assertThat(secondEntity.getVp()).isEqualTo(5);
    }

    @Test
    void onlyApTest() {
        PlayerDto firstDto = player(1L, 0, 0, 10);
        PlayerDto secondDto = player(2L, 0, 0, 5);

        PlayerEntity firstEntity = entityFromDb(1L, 0, 0);
        PlayerEntity secondEntity = entityFromDb(2L, 0, 0);

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isGreaterThanOrEqualTo(0);
        assertThat(firstEntity.getVp()).isEqualTo(10);
        assertThat(secondEntity.getVp()).isEqualTo(5);
    }

    @Test
    void vpOppTrackingTest() {
        PlayerDto firstDto = player(1L, 0, 8, 7);
        PlayerDto secondDto = player(2L, 0, 6, 5);

        PlayerEntity firstEntity = entityFromDb(1L, 0, 0);
        PlayerEntity secondEntity = entityFromDb(2L, 0, 0);

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getVpOpp()).isEqualTo(6 + 5);
        assertThat(secondEntity.getVpOpp()).isEqualTo(8 + 7);
    }

    private PlayerDto player(long id, int vp, int mp, int ap) {
        return PlayerDto.builder()
                .id(id)
                .name("TestPlayer" + id)
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

    private PlayerEntity entityFromDb(long id, int vp, int tp) {
        PlayerEntity e = new PlayerEntity();
        e.setId(id);
        e.setVp(vp);
        e.setTp(tp);
        e.setTotalAp(0);
        e.setTotalMp(0);
        e.setVpOpp(0);
        return e;
    }
}


