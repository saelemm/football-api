package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.TeamId;
import entity.Transfer;
import org.springframework.stereotype.Service;
import port.ITeamRepository;
import port.ITransferRepository;
import usecase.GetTeamTransferHistoryUseCase;

import java.util.List;

@Service
public class GetTeamTransferHistoryService implements GetTeamTransferHistoryUseCase {

    private final ITransferRepository transferRepository;
    private final ITeamRepository teamRepository;

    public GetTeamTransferHistoryService(ITransferRepository transferRepository, ITeamRepository teamRepository) {
        this.transferRepository = transferRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public List<Transfer> findAllTransfers(TeamId teamId) {
        validateTeamExists(teamId);
        return transferRepository.findByTeamId(teamId);
    }

    @Override
    public List<Transfer> findOutgoing(TeamId teamId) {
        validateTeamExists(teamId);
        return transferRepository.findOutgoingTransfers(teamId);
    }

    @Override
    public List<Transfer> findIncoming(TeamId teamId) {
        validateTeamExists(teamId);
        return transferRepository.findIncomingTransfers(teamId);
    }


    private void validateTeamExists(TeamId teamId) {
        teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamNotFoundException("Équipe introuvable : " + teamId.value()));
    }
}

