package org.example.tournamentapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.HashSet;
import java.util.Set;

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
    private Set<PlayerEntity> opponents = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    private int ap;
    private int mp;
    private int vp;
    private int tp;

    private int totalAp;
    private int totalMp;
    private int vpOpp;

}
