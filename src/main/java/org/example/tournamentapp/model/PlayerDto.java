package org.example.tournamentapp.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tournamentapp.validation.CreateGroup;
import org.example.tournamentapp.validation.ScoreGroup;
import org.example.tournamentapp.validation.SelectGroup;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {
    @NotNull(groups = SelectGroup.class, message = "Выберите игрока")
    private Long id;

    @NotBlank(groups= CreateGroup.class,message = "У игрока должно быть имя")
    @Size(groups= CreateGroup.class,min = 2, max = 35, message = "Имя должно содержать от 2 до 35 символов")
    private String name;
    private String faction;

    @Builder.Default
    private Set<String> namesPlayed=new HashSet<>();

    @NotNull(groups = ScoreGroup.class, message = "Введите AP")
    @Min(value = 0, groups = ScoreGroup.class, message = "AP не может быть меньше 0")
    @Max(value = 20, groups = ScoreGroup.class, message = "AP слишком большой")
    private Integer  ap;

    @NotNull(groups = ScoreGroup.class, message = "Введите MP")
    @Min(value = 0, groups = ScoreGroup.class, message = "MP не может быть меньше 0")
    @Max(value = 20, groups = ScoreGroup.class, message = "MP слишком большой")
    private Integer  mp;

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
