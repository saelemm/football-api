package com.foot.application.service;

import Errors.PlayerNotFoundException;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import org.springframework.stereotype.Service;
import port.IPlayerRepository;
import usecase.UpdatePlayerPerformanceUseCase;

@Service
public class UpdatePlayerPerformanceService implements UpdatePlayerPerformanceUseCase {

    private final IPlayerRepository playerRepository;

    public UpdatePlayerPerformanceService(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void execute(PlayerId playerId, Float newPerformance) {
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new PlayerNotFoundException("Joueur introuvable : " + playerId.value()));

        Player updated = player.updatePerformance(new Note(newPerformance));
        playerRepository.save(updated);
    }
}

