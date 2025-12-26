package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalculateService {

    private final PlayerService playerService;

    @Transactional
    public void calculateFromPairsWrapper(PairsWrapper pairsWrapper) {
        for (PairDto pair : pairsWrapper.getPairs()) {
            calculate(pair.getFirstPlayer(), pair.getSecondPlayer());
        }

    }

    @Transactional
    public void calculate(PlayerDto firstPlayerResult, PlayerDto secondPlayerResult) {
        PlayerEntity firstPlayer = playerService.getPlayerById(firstPlayerResult.getId());
        PlayerEntity secondPlayer = playerService.getPlayerById(secondPlayerResult.getId());

        int firstPlayerEarnedVP = firstPlayerResult.getAp() + firstPlayerResult.getMp();
        int secondPlayerEarnedVP = secondPlayerResult.getAp() + secondPlayerResult.getMp();

        int firstPlayerCalculatedTP = calculateTp(
                firstPlayerResult.getTp(), firstPlayerEarnedVP, secondPlayerEarnedVP, firstPlayerResult.getMp(), firstPlayerResult.getAp(),
                secondPlayerResult.getMp(), secondPlayerResult.getAp());

        int secondPlayerCalculatedTP = calculateTp(
                secondPlayerResult.getTp(), secondPlayerEarnedVP, firstPlayerEarnedVP, secondPlayerResult.getMp(), secondPlayerResult.getAp(),
                firstPlayerResult.getMp(), firstPlayerResult.getAp());


        firstPlayer.setTp(firstPlayerCalculatedTP);
        firstPlayer.setVp(firstPlayerResult.getVp() + firstPlayerEarnedVP);
        firstPlayer.setTotalMp(firstPlayer.getTotalMp() + firstPlayerResult.getMp());
        firstPlayer.setTotalAp(firstPlayer.getTotalAp() + firstPlayerResult.getAp());

        secondPlayer.setTp(secondPlayerCalculatedTP);
        secondPlayer.setVp(secondPlayerResult.getVp() + secondPlayerEarnedVP);
        secondPlayer.setTotalMp(secondPlayer.getTotalMp() + secondPlayerResult.getMp());
        secondPlayer.setTotalAp(secondPlayer.getTotalAp() + secondPlayerResult.getAp());

        firstPlayer.setVpOpp(firstPlayer.getVpOpp() + secondPlayerResult.getAp() + secondPlayerResult.getMp());
        secondPlayer.setVpOpp(secondPlayer.getVpOpp() + firstPlayerResult.getAp() + firstPlayerResult.getMp());

        playerService.savePlayer(firstPlayer);
        playerService.savePlayer(secondPlayer);
    }

    private int calculateTp(int currentTp, int newVp, int otherVp, int mp, int ap, int otherMp, int otherAp) {
        boolean allGreater = newVp > otherVp && mp > otherMp && ap > otherAp;
        boolean vpGreater = newVp > otherVp;
        boolean mpOrApNotStrictlyGreater = (mp >= otherMp || ap >= otherAp);
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
