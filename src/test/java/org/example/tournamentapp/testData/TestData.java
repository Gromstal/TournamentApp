package org.example.tournamentapp.testData;

import org.example.tournamentapp.model.Player;


import java.util.List;

public class TestData {

    public List<Player> getSetupPlayers (){
        return List.of(
                new Player("name1","faction1",0,0,0,0),
                new Player("name2","faction2",0,0,0,0),
                new Player("name3","faction3",0,0,0,0),
                new Player("name4","faction4",0,0,0,0),
                new Player("name5","faction2",0,0,0,0),
                new Player("name6","faction1",0,0,0,0),
                new Player("name7","faction3",0,0,0,0),
                new Player("name8","faction4",0,0,0,0),
                new Player("name9","faction5",0,0,0,0),
                new Player("name10","faction6",0,0,0,0)

        );

    }
}
