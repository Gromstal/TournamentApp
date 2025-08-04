package org.example.tournamentapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String faction;

    @ManyToMany
    @JoinTable(
            name = "player_opponents",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "opponent_id")
    )
    private List<PlayerEntity> opponents = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    private int ap;
    private int mp;
    private int vp;
    private int tp;

    private int totalAp;
    private int totalMp;

}
