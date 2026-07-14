package com.foot.adapter.persistence.jpa.repository;

import com.foot.adapter.persistence.jpa.entity.TransferJpa;
import com.foot.adapter.persistence.jpa.repository.spring.TransferSpringRepository;
import entity.PlayerId;
import entity.Price;
import entity.TeamId;
import entity.Transfer;
import entity.TransferId;
import port.ITransferRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pagination.PagedResult;
import org.springframework.stereotype.Repository;

@Repository
public class TransferRepositoryAdapter implements ITransferRepository {

    private final TransferSpringRepository transferRepository;

    public TransferRepositoryAdapter(TransferSpringRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    public Optional<Transfer> findById(TransferId id) {
        return transferRepository.findById(id.value()).map(TransferRepositoryAdapter::toDomain);
    }

    @Override
    public Long save(Transfer transfer) {
        TransferJpa saved = transferRepository.save(toJpa(transfer));
        return saved.getId();
    }

    @Override
    public PagedResult<Transfer> findByPlayerId(PlayerId playerId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = mapTransferSortField(sortBy);
        Page<TransferJpa> transferPage = transferRepository.findByPlayerId(
            playerId.value(),
            PageRequest.of(page, size, Sort.by(sortDirection, sortField))
        );

        return toPagedResult(transferPage, sortField, sortDirection);
    }

    @Override
    public PagedResult<Transfer> findByTeamId(TeamId teamId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = mapTransferSortField(sortBy);
        Page<TransferJpa> transferPage = transferRepository.findBySourceTeamIdOrTargetTeamId(
            teamId.value(),
            teamId.value(),
            PageRequest.of(page, size, Sort.by(sortDirection, sortField))
        );

        return toPagedResult(transferPage, sortField, sortDirection);
    }

    @Override
    public PagedResult<Transfer> findOutgoingTransfers(TeamId teamId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = mapTransferSortField(sortBy);
        Page<TransferJpa> transferPage = transferRepository.findBySourceTeamId(
            teamId.value(),
            PageRequest.of(page, size, Sort.by(sortDirection, sortField))
        );

        return toPagedResult(transferPage, sortField, sortDirection);
    }

    @Override
    public PagedResult<Transfer> findIncomingTransfers(TeamId teamId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = mapTransferSortField(sortBy);
        Page<TransferJpa> transferPage = transferRepository.findByTargetTeamId(
            teamId.value(),
            PageRequest.of(page, size, Sort.by(sortDirection, sortField))
        );

        return toPagedResult(transferPage, sortField, sortDirection);
    }

    private PagedResult<Transfer> toPagedResult(Page<TransferJpa> transferPage, String sortField, Sort.Direction sortDirection) {
        List<Transfer> content = transferPage.stream()
            .map(TransferRepositoryAdapter::toDomain)
            .collect(Collectors.toList());

        return new PagedResult<>(
            content,
            transferPage.getNumber(),
            transferPage.getSize(),
            transferPage.getTotalElements(),
            transferPage.getTotalPages(),
            transferPage.isFirst(),
            transferPage.isLast(),
            sortField,
            sortDirection.name().toLowerCase()
        );
    }

    private String mapTransferSortField(String sortBy) {
        if (sortBy == null) {
            return "transferDate";
        }

        return switch (sortBy.toLowerCase()) {
            case "date", "transferdate" -> "transferDate";
            case "transferprice", "price", "prix" -> "transferPrice";
            case "player", "playerid", "joueur" -> "playerId";
            default -> "transferDate";
        };
    }

    static Transfer toDomain(TransferJpa jpa) {
        return new Transfer(
            new TransferId(jpa.getId()),
            new PlayerId(jpa.getPlayerId()),
            jpa.getSourceTeamId() == null ? null : new TeamId(jpa.getSourceTeamId()),
            new TeamId(jpa.getTargetTeamId()),
            new Price(jpa.getTransferPrice()),
            jpa.getTransferDate()
        );
    }

    private TransferJpa toJpa(Transfer transfer) {
        TransferJpa jpa = new TransferJpa();
        Long id = transfer.transferId().value();
        jpa.setId(id == 0L ? null : id);
        jpa.setPlayerId(transfer.playerId().value());
        jpa.setSourceTeamId(transfer.sourceTeamId() == null ? null : transfer.sourceTeamId().value());
        jpa.setTargetTeamId(transfer.targetTeamId().value());
        jpa.setTransferPrice(transfer.transferPrice().value());
        jpa.setTransferDate(transfer.transferDate());
        return jpa;
    }
}

