package org.example.tournamentapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class PairingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pairing_seq")
    @SequenceGenerator(name = "pairing_seq", sequenceName = "pairing_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_player_id")
    private PlayerEntity firstPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_player_id")
    private PlayerEntity secondPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    private int currentTour;
}
