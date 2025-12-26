package org.example.tournamentapp.service;

import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.PlayerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateServiceTest {

    @InjectMocks
    private CalculateService service;

    @Mock
    private PlayerService playerService;

    @Test
    void tp4_0Test() {
        PlayerDto firstDto = player(1L, 0, 10, 10);
        PlayerDto secondDto = player(2L, 0, 5, 5);

        PlayerEntity firstEntity = entityFromDb(1L, firstDto.getVp(), firstDto.getTp());
        PlayerEntity secondEntity = entityFromDb(2L, secondDto.getVp(), secondDto.getTp());

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(4);
        assertThat(secondEntity.getTp()).isEqualTo(0);

        assertThat(firstEntity.getVp()).isEqualTo((20));
        assertThat(secondEntity.getVp()).isEqualTo((10));

        assertThat(firstEntity.getVpOpp()).isEqualTo(5 + 5);
        assertThat(secondEntity.getVpOpp()).isEqualTo(10 + 10);

        verify(playerService).savePlayer(firstEntity);
        verify(playerService).savePlayer(secondEntity);
        verifyNoMoreInteractions(playerService);
    }

    @Test
    void tp4_0v2Test() {
        PlayerDto firstDto = player(1L, 0, 1, 2);
        PlayerDto secondDto = player(2L, 0, 0, 1);

        PlayerEntity firstEntity = entityFromDb(1L, firstDto.getVp(), firstDto.getTp());
        PlayerEntity secondEntity = entityFromDb(2L, secondDto.getVp(), secondDto.getTp());

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(4);
        assertThat(secondEntity.getTp()).isEqualTo(0);

        verify(playerService).savePlayer(firstEntity);
        verify(playerService).savePlayer(secondEntity);
        verifyNoMoreInteractions(playerService);
    }

    @Test
    void tp3_1Test() {
        PlayerDto firstDto = player(1L, 0, 4, 9);
        PlayerDto secondDto = player(2L, 0, 5, 7);

        PlayerEntity firstEntity = entityFromDb(1L, firstDto.getVp(), firstDto.getTp());
        PlayerEntity secondEntity = entityFromDb(2L, secondDto.getVp(), secondDto.getTp());

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(3);
        assertThat(secondEntity.getTp()).isEqualTo(1);

        assertThat(firstEntity.getVp()).isEqualTo((4 + 9));
        assertThat(secondEntity.getVp()).isEqualTo((5 + 7));

        assertThat(firstEntity.getVpOpp()).isEqualTo(5 + 7);
        assertThat(secondEntity.getVpOpp()).isEqualTo(4 + 9);

        verify(playerService).savePlayer(firstEntity);
        verify(playerService).savePlayer(secondEntity);
        verifyNoMoreInteractions(playerService);
    }

    @Test
    void tp3_1v2Test() {
        PlayerDto firstDto = player(1L, 0, 5, 9);
        PlayerDto secondDto = player(2L, 0, 5, 7);

        PlayerEntity firstEntity = entityFromDb(1L, firstDto.getVp(), firstDto.getTp());
        PlayerEntity secondEntity = entityFromDb(2L, secondDto.getVp(), secondDto.getTp());

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(3);
        assertThat(secondEntity.getTp()).isEqualTo(1);

        assertThat(firstEntity.getVp()).isEqualTo((5 + 9));
        assertThat(secondEntity.getVp()).isEqualTo((5 + 7));

        verify(playerService).savePlayer(firstEntity);
        verify(playerService).savePlayer(secondEntity);
        verifyNoMoreInteractions(playerService);
    }

    @Test
    void tieTest() {
        PlayerDto firstDto = player(1L, 0, 5, 5);
        PlayerDto secondDto = player(2L, 0, 5, 5);

        PlayerEntity firstEntity = entityFromDb(1L, firstDto.getVp(), firstDto.getTp());
        PlayerEntity secondEntity = entityFromDb(2L, secondDto.getVp(), secondDto.getTp());

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(2);
        assertThat(secondEntity.getTp()).isEqualTo(2);

        verify(playerService).savePlayer(firstEntity);
        verify(playerService).savePlayer(secondEntity);
        verifyNoMoreInteractions(playerService);
    }

    @Test
    void tieV2Test() {
        PlayerDto firstDto = player(1L, 0, 4, 5);
        PlayerDto secondDto = player(2L, 0, 5, 4);

        PlayerEntity firstEntity = entityFromDb(1L, firstDto.getVp(), firstDto.getTp());
        PlayerEntity secondEntity = entityFromDb(2L, secondDto.getVp(), secondDto.getTp());

        when(playerService.getPlayerById(1L)).thenReturn(firstEntity);
        when(playerService.getPlayerById(2L)).thenReturn(secondEntity);

        service.calculate(firstDto, secondDto);

        assertThat(firstEntity.getTp()).isEqualTo(2);
        assertThat(secondEntity.getTp()).isEqualTo(2);

        verify(playerService).savePlayer(firstEntity);
        verify(playerService).savePlayer(secondEntity);
        verifyNoMoreInteractions(playerService);
    }


    private PlayerDto player(long id, int vp, int mp, int ap) {
        return PlayerDto.builder()
                .id(id)
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