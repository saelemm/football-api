package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.Player;
import entity.Team;
import entity.TeamId;
import org.springframework.stereotype.Service;
import port.IPlayerRepository;
import port.ITeamRepository;
import usecase.GetTeamDetailsUseCase;

import java.util.List;

@Service
public class GetTeamDetailsService implements GetTeamDetailsUseCase {

    private final ITeamRepository teamRepository;
    private final IPlayerRepository playerRepository;

    public GetTeamDetailsService(ITeamRepository teamRepository, IPlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Team execute(TeamId teamId) {
        return teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamNotFoundException("Équipe introuvable : " + teamId.value()));
    }

    @Override
    public List<Player> findCurrentPlayers(TeamId teamId) {
        execute(teamId);
        return playerRepository.findByTeamId(teamId);
    }

    @Override
    public List<Player> findTitulaires(TeamId teamId) {
        return findCurrentPlayers(teamId).stream()
            .filter(p -> p.stats().isTitulaire())
            .toList();
    }

    @Override
    public List<Player> findRemplacants(TeamId teamId) {
        return findCurrentPlayers(teamId).stream()
            .filter(p -> !p.stats().isTitulaire())
            .toList();
    }
}

