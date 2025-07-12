package org.example.tournamentapp.service;

import org.example.tournamentapp.model.Player;
import org.springframework.stereotype.Service;

@Service
public class CalculateService {

    public void calculate(Player first, Player second) {
        int newVpFirst = first.getVp() + first.getAp() + first.getMp();
        int newVpSecond = second.getVp() + second.getAp() + second.getMp();

        int tpFirst = calculateTp(
                first.getTp(), newVpFirst, newVpSecond, first.getMp(), first.getAp(),
                second.getMp(), second.getAp());

        int tpSecond = calculateTp(
                second.getTp(), newVpSecond, newVpFirst, second.getMp(), second.getAp(),
                first.getMp(), first.getAp());

        first.setTp(tpFirst);
        second.setTp(tpSecond);

        first.setVp(newVpFirst);
        second.setVp(newVpSecond);

        first.setTotalMp(first.getTotalMp() + first.getMp());
        first.setTotalAp(first.getTotalAp() + first.getAp());

        second.setTotalMp(second.getTotalMp() + second.getMp());
        second.setTotalAp(second.getTotalAp() + second.getAp());
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
