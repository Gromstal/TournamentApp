package org.example.tournamentapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Pairing {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pairing_seq")
    @SequenceGenerator(name = "pairing_seq", sequenceName = "pairing_seq", allocationSize = 50)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_player_id")
    private PlayerEntity firstPlayer;

    @ManyToOne
    @JoinColumn(name = "second_player_id")
    private PlayerEntity secondPlayer;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    private int currentTour;
}
