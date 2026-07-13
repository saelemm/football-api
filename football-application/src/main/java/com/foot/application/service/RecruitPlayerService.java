package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.Note;
import entity.Player;
import entity.PlayerId;
import entity.PlayerIdentifier;
import entity.PlayerStat;
import entity.PlayerVersion;
import entity.PositionEnum;
import entity.Price;
import entity.Team;
import entity.TeamId;
import entity.Transfer;
import entity.TransferId;
import org.springframework.stereotype.Service;
import port.IPlayerRepository;
import port.ITeamRepository;
import port.ITransferRepository;
import usecase.RecruitPlayerUseCase;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class RecruitPlayerService implements RecruitPlayerUseCase {

    private final ITeamRepository teamRepository;
    private final IPlayerRepository playerRepository;
    private final ITransferRepository transferRepository;

    public RecruitPlayerService(ITeamRepository teamRepository,
                                IPlayerRepository playerRepository,
                                ITransferRepository transferRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.transferRepository = transferRepository;
    }

    @Override
    public Long execute(String firstName, String lastName, String acronym,
                        PositionEnum position, Float performance,
                        BigDecimal marketPrice, Long teamId) {

        TeamId tId = new TeamId(teamId);
        Team team = teamRepository.findById(tId)
            .orElseThrow(() -> new TeamNotFoundException("Équipe introuvable : " + teamId));

        Price price = new Price(marketPrice);

        // Création du joueur avec id=0L auto-générer par JPA
        Player player = new Player(
            new PlayerIdentifier(new PlayerId(0L), firstName, lastName, acronym, tId),
            new PlayerStat(position, new Note(performance), price, false),
            new PlayerVersion(0, new Date(), new Date())
        );
        Long playerId = playerRepository.save(player);

        // Création du transfert initial (pas d'équipe source)
        Transfer transfer = new Transfer(
            new TransferId(0L), new PlayerId(playerId), null, tId, price, new Date()
        );
        transferRepository.save(transfer);

        // Mise à jour de l'équipe
        Team updatedTeam = team.addPlayer(new PlayerId(playerId), price, transfer);
        teamRepository.save(updatedTeam);

        return playerId;
    }
}

