package org.example.tournamentapp.repository;

import org.example.tournamentapp.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerEntity,Long> {


    List<PlayerEntity> findAllByTournamentId(Long tournamentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PlayerEntity p where p.tournament.id = :tournamentId")
    void deleteByTournament_Id(Long tournamentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    delete from player_opponents po
    using player_entity p
    where (po.player_id = p.id and p.tournament_id = :tournamentId)
       or (po.opponent_id = p.id and p.tournament_id = :tournamentId)
    """, nativeQuery = true)
    void deleteOpponentsLinksByTournamentId(@Param("tournamentId") Long tournamentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        insert into player_opponents (player_id, opponent_id)
        values (:playerId, :opponentId)
        on conflict do nothing
        """, nativeQuery = true)
    void saveOpponents(@Param("playerId") Long playerId,
                       @Param("opponentId") Long opponentId);

    @Query(value = """
    select po.player_id, o.name
    from player_opponents po
    join player_entity p on p.id = po.player_id
    join player_entity o on o.id = po.opponent_id
    where p.tournament_id = :tournamentId
""", nativeQuery = true)
    List<Object[]> findOpponentNames(@Param("tournamentId") Long tournamentId);
}
