package com.foot.application.service;

import Errors.TeamNotFoundException;
import entity.TeamId;
import entity.Transfer;
import org.springframework.stereotype.Service;
import pagination.PagedResult;
import port.ITeamRepository;
import port.ITransferRepository;
import usecase.GetTeamTransferHistoryUseCase;

@Service
public class GetTeamTransferHistoryService implements GetTeamTransferHistoryUseCase {

    private final ITransferRepository transferRepository;
    private final ITeamRepository teamRepository;

    public GetTeamTransferHistoryService(ITransferRepository transferRepository, ITeamRepository teamRepository) {
        this.transferRepository = transferRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public PagedResult<Transfer> findAllTransfers(TeamId teamId, int page, int size, String sortBy, String direction) {
        validateTeamExists(teamId);
        return transferRepository.findByTeamId(teamId, normalizePage(page), normalizeSize(size), sortBy, direction);
    }

    @Override
    public PagedResult<Transfer> findOutgoing(TeamId teamId, int page, int size, String sortBy, String direction) {
        validateTeamExists(teamId);
        return transferRepository.findOutgoingTransfers(teamId, normalizePage(page), normalizeSize(size), sortBy, direction);
    }

    @Override
    public PagedResult<Transfer> findIncoming(TeamId teamId, int page, int size, String sortBy, String direction) {
        validateTeamExists(teamId);
        return transferRepository.findIncomingTransfers(teamId, normalizePage(page), normalizeSize(size), sortBy, direction);
    }


    /**
     * Rechercher l'existence d'une team grace à son Id.
     *
     * @param teamId ID de l'équipe à vérifier
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    private void validateTeamExists(TeamId teamId) {
        teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamNotFoundException("Équipe introuvable : " + teamId.value()));
    }

    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {
        int bounded = size <= 0 ? 20 : size;
        return Math.min(bounded, 100);
    }
}

