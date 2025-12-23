package org.example.tournamentapp.testData;

import org.example.tournamentapp.model.PlayerDto;


import java.util.List;

public class TestData {

    public List<PlayerDto> getSetupPlayers (){
        return List.of(
                new PlayerDto("Паша",    "faction1", 18, 12, 10,    7,  9),
                new PlayerDto("Даня",    "faction2", 16, 19, 18,    4,  8),
                new PlayerDto("Витя",    "faction3", 16, 19, 15,    9,  7),
                new PlayerDto("Настя",   "faction4", 16, 18, 20,    6,  6),
                new PlayerDto("Михаил",  "faction2", 16, 18, 20,    5,  5),
                new PlayerDto("Иван",    "faction1", 14, 20, 20,    8,  8),
                new PlayerDto("Тигран",  "faction3", 14, 20, 19,    9,  9),
                new PlayerDto("Евгений", "faction4", 14, 18, 20,    7, 10),
                new PlayerDto("Андрей П","faction5", 14, 18, 20,    6, 10),
                new PlayerDto("Андрей Н","faction6", 12, 20, 20,   10, 10)

        );

    }
}
