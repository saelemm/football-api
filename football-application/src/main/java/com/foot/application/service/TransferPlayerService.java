package com.foot.application.service;

import Errors.PlayerNotFoundException;
import Errors.TeamNotFoundException;
import entity.Player;
import entity.PlayerId;
import entity.Price;
import entity.Team;
import entity.TeamId;
import entity.Transfer;
import entity.TransferId;
import org.springframework.stereotype.Service;
import port.IPlayerRepository;
import port.ITeamRepository;
import port.ITransferRepository;
import usecase.TransferPlayerUseCase;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class TransferPlayerService implements TransferPlayerUseCase {

    private final IPlayerRepository playerRepository;
    private final ITeamRepository teamRepository;
    private final ITransferRepository transferRepository;

    public TransferPlayerService(IPlayerRepository playerRepository,
                                 ITeamRepository teamRepository,
                                 ITransferRepository transferRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.transferRepository = transferRepository;
    }

    @Override
    public Transfer execute(PlayerId playerId, TeamId sourceTeamId, TeamId targetTeamId, BigDecimal transferPrice) {
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new PlayerNotFoundException("Joueur introuvable : " + playerId.value()));

        Team targetTeam = teamRepository.findById(targetTeamId)
            .orElseThrow(() -> new TeamNotFoundException("Équipe cible introuvable : " + targetTeamId.value()));

        Price price = new Price(transferPrice);

        Transfer transfer = new Transfer(
            new TransferId(0L), playerId, sourceTeamId, targetTeamId, price, new Date()
        );
        transferRepository.save(transfer);

        // Mise à jour joueur
        Player updatedPlayer = player.transferTo(targetTeamId);
        playerRepository.save(updatedPlayer);

        // Mise à jour équipe source
        if (sourceTeamId != null) {
            teamRepository.findById(sourceTeamId).ifPresent(sourceTeam -> {
                Team updatedSource = sourceTeam.removePlayer(playerId, price, transfer);
                teamRepository.save(updatedSource);
            });
        }

        // Mise à jour équipe cible
        Team updatedTarget = targetTeam.addPlayer(playerId, price, transfer);
        teamRepository.save(updatedTarget);

        return transfer;
    }
}

