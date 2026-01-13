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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateServiceWrapperTest {

    @InjectMocks
    private CalculateService service;
    @Mock
    private PlayerService playerService;

    @Test
    void wrapperMultiPairsTest() {
        PlayerDto p1 = player(1L, 0, 5, 5);
        PlayerDto p2 = player(2L, 0, 5, 5);
        PlayerDto p3 = player(3L, 0, 10, 10);
        PlayerDto p4 = player(4L, 0, 5, 5);

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(
                new PairDto(p1, p2),
                new PairDto(p3, p4)
        ));

        when(playerService.getPlayerById(1L)).thenReturn(entityFromDb(1L, 0, 0));
        when(playerService.getPlayerById(2L)).thenReturn(entityFromDb(2L, 0, 0));
        when(playerService.getPlayerById(3L)).thenReturn(entityFromDb(3L, 0, 0));
        when(playerService.getPlayerById(4L)).thenReturn(entityFromDb(4L, 0, 0));

        service.calculateFromPairsWrapper(100L, wrapper);

        verify(playerService, times(4)).getPlayerById(anyLong());
        verify(playerService, times(4)).savePlayer(any(PlayerEntity.class));
    }

    @Test
    void wrapperEmptyTest() {
        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(new ArrayList<>());

        service.calculateFromPairsWrapper(100L, wrapper);

        verify(playerService, never()).getPlayerById(anyLong());
        verify(playerService, never()).savePlayer(any(PlayerEntity.class));
    }

    @Test
    void wrapperSingleTest() {
        PlayerDto p1 = player(1L, 0, 10, 10);
        PlayerDto p2 = player(2L, 0, 5, 5);

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(new PairDto(p1, p2)));

        when(playerService.getPlayerById(1L)).thenReturn(entityFromDb(1L, 0, 0));
        when(playerService.getPlayerById(2L)).thenReturn(entityFromDb(2L, 0, 0));

        service.calculateFromPairsWrapper(100L, wrapper);

        verify(playerService, times(2)).getPlayerById(anyLong());
        verify(playerService, times(2)).savePlayer(any(PlayerEntity.class));
    }

    @Test
    void wrapperTpAccumTest() {
        PlayerDto p1 = player(1L, 5, 10, 10);
        PlayerDto p2 = player(2L, 3, 5, 5);

        PairsWrapper wrapper = new PairsWrapper();
        wrapper.setPairs(List.of(new PairDto(p1, p2)));

        PlayerEntity e1 = entityFromDb(1L, 20, 5);
        PlayerEntity e2 = entityFromDb(2L, 10, 3);

        when(playerService.getPlayerById(1L)).thenReturn(e1);
        when(playerService.getPlayerById(2L)).thenReturn(e2);

        service.calculateFromPairsWrapper(100L, wrapper);

        verify(playerService, times(1)).savePlayer(e1);
        verify(playerService, times(1)).savePlayer(e2);
    }

    @Test
    void vpOppAccumTest() {
        PlayerDto p1 = player(1L, 0, 10, 5);
        PlayerDto p2 = player(2L, 0, 5, 10);

        PlayerEntity e1 = entityFromDb(1L, 0, 0);
        e1.setVpOpp(0);
        PlayerEntity e2 = entityFromDb(2L, 0, 0);
        e2.setVpOpp(0);

        when(playerService.getPlayerById(1L)).thenReturn(e1);
        when(playerService.getPlayerById(2L)).thenReturn(e2);

        service.calculate(p1, p2);

        verify(playerService, times(1)).savePlayer(e1);
        verify(playerService, times(1)).savePlayer(e2);
    }

    @Test
    void totalMpApAccumTest() {
        PlayerDto p1 = player(1L, 0, 10, 5);
        PlayerDto p2 = player(2L, 0, 5, 10);

        PlayerEntity e1 = entityFromDb(1L, 0, 0);
        e1.setTotalMp(10);
        e1.setTotalAp(5);
        PlayerEntity e2 = entityFromDb(2L, 0, 0);
        e2.setTotalMp(5);
        e2.setTotalAp(10);

        when(playerService.getPlayerById(1L)).thenReturn(e1);
        when(playerService.getPlayerById(2L)).thenReturn(e2);

        service.calculate(p1, p2);

        verify(playerService, times(1)).savePlayer(e1);
        verify(playerService, times(1)).savePlayer(e2);
    }

    private PlayerDto player(long id, int tp, int mp, int ap) {
        return PlayerDto.builder()
                .id(id)
                .name("Player" + id)
                .faction("Faction")
                .vp(0)
                .mp(mp)
                .ap(ap)
                .tp(tp)
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


