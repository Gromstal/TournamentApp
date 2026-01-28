package org.example.tournamentapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tournamentapp.entity.PlayerEntity;
import org.example.tournamentapp.model.record.CalculateContext;
import org.example.tournamentapp.model.PairDto;
import org.example.tournamentapp.model.PlayerDto;
import org.example.tournamentapp.wrapper.PairsWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateService {

    private final PlayerService playerService;

    @Transactional
    public void calculateFromPairsWrapper(Long tournamentId, PairsWrapper pairsWrapper) {
        for (PairDto pair : pairsWrapper.getPairs()) {
            calculate(pair.getFirstPlayer(), pair.getSecondPlayer());
        }
        log.info("Tournament {} Points calculated for all players", tournamentId);
    }


    public void calculate(PlayerDto firstPlayerResult, PlayerDto secondPlayerResult) {
        int firstPlayerEarnedVP = firstPlayerResult.getAp() + firstPlayerResult.getMp();
        int secondPlayerEarnedVP = secondPlayerResult.getAp() + secondPlayerResult.getMp();

        int firstPlayerMp = firstPlayerResult.getMp();
        int firstPlayerAp = firstPlayerResult.getAp();
        int secondPlayerMp = secondPlayerResult.getMp();
        int secondPlayerAp = secondPlayerResult.getAp();

        int firstPlayerCurrentTp = firstPlayerResult.getTp();
        int firstPlayerCalculatedTP = calculateTp(
                firstPlayerCurrentTp, firstPlayerEarnedVP, secondPlayerEarnedVP,
                firstPlayerMp, firstPlayerAp, secondPlayerMp, secondPlayerAp);

        int secondPlayerCurrentTp = secondPlayerResult.getTp();
        int secondPlayerCalculatedTP = calculateTp(
                secondPlayerCurrentTp, secondPlayerEarnedVP, firstPlayerEarnedVP,
                secondPlayerMp, secondPlayerAp, firstPlayerMp, firstPlayerAp);

        log.debug("Calculated points for players: {} {}, {} {}",
                firstPlayerResult.getId(),
                firstPlayerResult.getName(),
                secondPlayerResult.getId(),
                secondPlayerResult.getName());
        log.debug("Player {} {}: {} TP, {} VP, {} AP, {} MP",
                firstPlayerResult.getId(),
                firstPlayerResult.getName(),
                firstPlayerCalculatedTP,
                firstPlayerEarnedVP,
                firstPlayerResult.getAp(),
                firstPlayerResult.getMp()
        );
        log.debug("Player {} {}: {} TP, {} VP, {} AP, {} MP",
                secondPlayerResult.getId(),
                secondPlayerResult.getName(),
                secondPlayerCalculatedTP,
                secondPlayerEarnedVP,
                secondPlayerResult.getAp(),
                secondPlayerResult.getMp()
        );

        saveCalculatedPoints(new CalculateContext(firstPlayerResult,
                secondPlayerResult,
                firstPlayerEarnedVP,
                secondPlayerEarnedVP,
                firstPlayerCalculatedTP,
                secondPlayerCalculatedTP));
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

    private void saveCalculatedPoints(CalculateContext calculateContext) {
        PlayerDto firstPlayerResult = calculateContext.firstPlayerResult();
        PlayerDto secondPlayerResult = calculateContext.secondPlayerResult();

        PlayerEntity firstPlayer = playerService.getPlayerById(firstPlayerResult.getId());
        PlayerEntity secondPlayer = playerService.getPlayerById(secondPlayerResult.getId());

        firstPlayer.setTp(calculateContext.firstPlayerCalculatedTP());
        firstPlayer.setVp(firstPlayerResult.getVp() + calculateContext.firstPlayerEarnedVP());
        firstPlayer.setTotalMp(firstPlayer.getTotalMp() + firstPlayerResult.getMp());
        firstPlayer.setTotalAp(firstPlayer.getTotalAp() + firstPlayerResult.getAp());

        secondPlayer.setTp(calculateContext.secondPlayerCalculatedTP());
        secondPlayer.setVp(secondPlayerResult.getVp() + calculateContext.secondPlayerEarnedVP());
        secondPlayer.setTotalMp(secondPlayer.getTotalMp() + secondPlayerResult.getMp());
        secondPlayer.setTotalAp(secondPlayer.getTotalAp() + secondPlayerResult.getAp());

        firstPlayer.setVpOpp(firstPlayer.getVpOpp() + secondPlayerResult.getAp() + secondPlayerResult.getMp());
        secondPlayer.setVpOpp(secondPlayer.getVpOpp() + firstPlayerResult.getAp() + firstPlayerResult.getMp());

        log.debug("Points saved for players {} and {} ", firstPlayer.getId(), secondPlayer.getId());
        playerService.savePlayer(firstPlayer);
        playerService.savePlayer(secondPlayer);
    }
}
