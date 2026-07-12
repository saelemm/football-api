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
    public List<Transfer> findByPlayerId(PlayerId playerId) {
        return transferRepository.findByPlayerId(playerId.value()).stream()
            .map(TransferRepositoryAdapter::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByTeamId(TeamId teamId) {
        return transferRepository.findBySourceTeamIdOrTargetTeamId(teamId.value(), teamId.value()).stream()
            .map(TransferRepositoryAdapter::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findOutgoingTransfers(TeamId teamId) {
        return transferRepository.findBySourceTeamId(teamId.value()).stream()
            .map(TransferRepositoryAdapter::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findIncomingTransfers(TeamId teamId) {
        return transferRepository.findByTargetTeamId(teamId.value()).stream()
            .map(TransferRepositoryAdapter::toDomain)
            .collect(Collectors.toList());
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

