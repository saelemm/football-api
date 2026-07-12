package com.foot.application.service;

import Errors.DuplicateTeamException;
import entity.Team;
import entity.TeamId;
import entity.TeamIdentifier;
import entity.TeamStat;
import org.springframework.stereotype.Service;
import port.ITeamRepository;
import usecase.CreateTeamUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class CreateTeamService implements CreateTeamUseCase {

    private final ITeamRepository teamRepository;

    public CreateTeamService(ITeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Long execute(String name, String acronym, BigDecimal initialBudget) {
        if (teamRepository.findByName(name).isPresent()) {
            throw new DuplicateTeamException("Nom d'équipe déjà utilisé : " + name);
        }
        if (teamRepository.findByAcronym(acronym).isPresent()) {
            throw new DuplicateTeamException("Acronyme d'équipe déjà utilisé : " + acronym);
        }

        Team team = new Team(
            new TeamIdentifier(new TeamId(0L), name, acronym),
            new TeamStat(initialBudget, new Date(), new Date()),
            List.of(),
            List.of()
        );
        return teamRepository.save(team);
    }
}

