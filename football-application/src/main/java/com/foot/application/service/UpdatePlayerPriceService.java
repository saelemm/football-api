package com.foot.application.service;

import Errors.PlayerNotFoundException;
import entity.Player;
import entity.PlayerId;
import entity.PlayerStat;
import org.springframework.stereotype.Service;
import port.IPlayerRepository;
import usecase.UpdatePlayerPriceUseCase;

import java.math.BigDecimal;

@Service
public class UpdatePlayerPriceService implements UpdatePlayerPriceUseCase {

    private final IPlayerRepository playerRepository;

    public UpdatePlayerPriceService(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void execute(PlayerId playerId, BigDecimal newPrice) {

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Joueur introuvable : " + playerId));
        PlayerStat updatedStats = player.stats().updatePrice(player.stats().marketPrice().updateByAddition(newPrice));
        Player updatedPlayer = player.updateStats(updatedStats);
        playerRepository.save(updatedPlayer);

    }
}
