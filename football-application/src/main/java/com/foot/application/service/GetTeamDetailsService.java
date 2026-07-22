package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.Player;
import entity.Team;
import entity.TeamId;
import org.springframework.stereotype.Service;
import pagination.PagedResult;
import port.IPlayerRepository;
import port.ITeamRepository;
import usecase.GetTeamDetailsUseCase;

import java.util.List;
import java.util.Map;

@Service
public class GetTeamDetailsService implements GetTeamDetailsUseCase {

    private final ITeamRepository teamRepository;
    private final IPlayerRepository playerRepository;

    public GetTeamDetailsService(ITeamRepository teamRepository, IPlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public PagedResult<Team> findAllTeams(int page, int size, String sortBy, String direction) {
        return teamRepository.findAll(normalizePage(page), normalizeSize(size), sortBy, direction);
    }

    @Override
    public Map<Long, List<Player>> findCurrentPlayersByTeamIds(List<TeamId> teamIds) {
        return playerRepository.findByTeamIds(teamIds);
    }

    @Override
    public Team execute(TeamId teamId) {
        return teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamNotFoundException("Équipe introuvable : " + teamId.value()));
    }

    @Override
    public PagedResult<Player> findCurrentPlayers(TeamId teamId, int page, int size, String sortBy, String direction) {
        execute(teamId);
        return playerRepository.findByTeamId(teamId, normalizePage(page), normalizeSize(size), sortBy, direction);
    }

    @Override
    public PagedResult<Player> findTitulaires(TeamId teamId, int page, int size, String sortBy, String direction) {
        execute(teamId);
        return playerRepository.findByTeamIdAndTitulaire(teamId, true, normalizePage(page), normalizeSize(size), sortBy, direction);
    }

    @Override
    public PagedResult<Player> findRemplacants(TeamId teamId, int page, int size, String sortBy, String direction) {
        execute(teamId);
        return playerRepository.findByTeamIdAndTitulaire(teamId, false, normalizePage(page), normalizeSize(size), sortBy, direction);
    }

    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {
        int bounded = size <= 0 ? 20 : size;
        return Math.min(bounded, 100);
    }
}

