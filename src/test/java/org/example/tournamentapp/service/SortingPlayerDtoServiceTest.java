package org.example.tournamentapp.service;

import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.testData.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SortingPlayerDtoServiceTest {

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
}
