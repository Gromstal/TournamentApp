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
public class PlayerDto {
    private Long id;
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
    private int vpOpp;

    @Builder.Default
    private boolean inPair=false;

    public PlayerDto(String name, String faction, int TP, int VP, int VPopp, int totalAp, int totalMp) {
        this.name = name;
        this.faction = faction;
        this.tp = TP;
        this.vp = VP;
        this.vpOpp = VPopp;
        this.totalAp = totalAp;
        this.totalMp = totalMp;
    }
}
