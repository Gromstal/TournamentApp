package org.example.tournamentapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private String name;
    private String faction;

    @Builder.Default
    private List<String> namesPlayed=new ArrayList<>();

    private int ap;
    private int mp;
    private int vp;
    private int tp;

    private int totalAp;
    private int totalMp;

    @Builder.Default
    private boolean inPair=false;

    public Player(String name1, String faction1, int i, int i1, int i2, int i3) {
    }
}
