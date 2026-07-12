package com.foot.application.service;

import Errors.PlayerNotFoundException;
import entity.Player;
import entity.PlayerId;
import org.springframework.stereotype.Service;
import port.IPlayerRepository;
import usecase.GetPlayerDetailsUseCase;

@Service
public class GetPlayerDetailsService implements GetPlayerDetailsUseCase {

    private final IPlayerRepository playerRepository;

    public GetPlayerDetailsService(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player execute(PlayerId playerId) {
        return playerRepository.findById(playerId)
            .orElseThrow(() -> new PlayerNotFoundException("Joueur introuvable : " + playerId.value()));
    }
}

