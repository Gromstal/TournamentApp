package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalculateService {

    private final PlayerService playerService;

    @Transactional
    public void calculate(Player first, Player second) {
        PlayerEntity player1 =  playerService.getPlayerById(first.getId());
        PlayerEntity player2 =  playerService.getPlayerById(second.getId());

        int newVpFirst = first.getVp() + first.getAp() + first.getMp();
        int newVpSecond = second.getVp() + second.getAp() + second.getMp();

        int tpFirst = calculateTp(
                first.getTp(), newVpFirst, newVpSecond, first.getMp(), first.getAp(),
                second.getMp(), second.getAp());

        int tpSecond = calculateTp(
                second.getTp(), newVpSecond, newVpFirst, second.getMp(), second.getAp(),
                first.getMp(), first.getAp());


        player1.setTp(tpFirst);
        player1.setVp(newVpFirst);
        player1.setTotalMp(player1.getTotalMp() + first.getMp());
        player1.setTotalAp(player1.getTotalAp() + first.getAp());

        player2.setTp(tpSecond);
        player2.setVp(newVpSecond);
        player2.setTotalMp(player2.getTotalMp() + second.getMp());
        player2.setTotalAp(player2.getTotalAp() + second.getAp());

        player1.setVpOpp(player1.getVpOpp() + second.getAp() + second.getMp());
        player2.setVpOpp(player2.getVpOpp() + first.getAp() + first.getMp());

        playerService.savePlayer(player1);
        playerService.savePlayer(player2);
    }

    private int calculateTp(int currentTp, int newVp, int otherVp, int mp, int ap, int otherMp, int otherAp) {
        boolean allGreater = newVp > otherVp && mp > otherMp && ap > otherAp;
        boolean vpGreater = newVp > otherVp;
        boolean mpOrApNotStrictlyGreater = !(mp > otherMp && ap > otherAp);
        boolean vpEqual = newVp == otherVp;
        boolean vpLess = newVp < otherVp;
        boolean mpOrApAtLeastEqual = (mp >= otherMp) || (ap >= otherAp);

        if (allGreater) {
            return currentTp + 4;
        } else if (vpGreater && mpOrApNotStrictlyGreater) {
            return currentTp + 3;
        } else if (vpEqual) {
            return currentTp + 2;
        } else if (vpLess && mpOrApAtLeastEqual) {
            return currentTp + 1;
        } else {
            return currentTp;
        }
    }
}
