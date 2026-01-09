package org.example.tournamentapp.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {
    @NotNull
    private Long id;
    private String name;
    private String faction;

    @Builder.Default
    private Set<String> namesPlayed=new HashSet<>();

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
