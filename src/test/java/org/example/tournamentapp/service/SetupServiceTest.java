package org.example.tournamentapp.service;
import org.example.tournamentapp.builder.ProxyBotBuilder;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PlayerListWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetupServiceTest {

    @Mock
    private ProxyBotBuilder proxyBotBuilder;

    @Mock
    private PlayerListWrapper playerListWrapper;

    @Mock
    private SortingPlayerService sortingPlayerService;

    @InjectMocks
    private SetupService setupService;


}
