package com.foot.adapter.persistence.jpa.repository;

import com.foot.adapter.persistence.jpa.AbstractJpaContainerTest;
import com.foot.adapter.persistence.jpa.entity.PlayerJpa;
import com.foot.adapter.persistence.jpa.entity.TeamJpa;
import com.foot.adapter.persistence.jpa.repository.spring.PlayerSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TeamSpringRepository;
import com.foot.adapter.persistence.jpa.repository.spring.TransferSpringRepository;
import entity.PlayerId;
import entity.PositionEnum;
import entity.Price;
import entity.TeamId;
import entity.Transfer;
import entity.TransferId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pagination.PagedResult;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests JPA de TransferRepositoryAdapter")
class TransferRepositoryAdapterTest extends AbstractJpaContainerTest {

    @Autowired
    private TransferRepositoryAdapter adapter;

    @Autowired
    private TeamSpringRepository teamSpringRepository;

    @Autowired
    private PlayerSpringRepository playerSpringRepository;

    @Autowired
    private TransferSpringRepository transferSpringRepository;

    @BeforeEach
    void cleanDb() {
        transferSpringRepository.deleteAll();
        playerSpringRepository.deleteAll();
        teamSpringRepository.deleteAll();
    }

    @Test
    @DisplayName("doit sauvegarder puis retrouver un transfert par id")
    void shouldSaveAndFindById() {
        Long teamAId = saveTeam("A", "A");
        Long teamBId = saveTeam("B", "B");
        Long playerId = savePlayer(teamAId);

        Transfer transfer = new Transfer(
            new TransferId(0L),
            new PlayerId(playerId),
            new TeamId(teamAId),
            new TeamId(teamBId),
            new Price(BigDecimal.valueOf(7500.0)),
            new Date()
        );

        Long transferId = adapter.save(transfer);
        Optional<Transfer> loaded = adapter.findById(new TransferId(transferId));

        assertTrue(loaded.isPresent());
        assertEquals(playerId, loaded.get().playerId().value());
        assertEquals(teamAId, loaded.get().sourceTeamId().value());
        assertEquals(teamBId, loaded.get().targetTeamId().value());
    }

    @Test
    @DisplayName("doit filtrer les transferts par joueur")
    void shouldFindByPlayerId() {
        Long teamCId = saveTeam("C", "C");
        Long teamDId = saveTeam("D", "D");
        Long playerId = savePlayer(teamCId);

        Date older = new Date(System.currentTimeMillis() - 10_000);
        Date newer = new Date();
        adapter.save(new Transfer(new TransferId(0L), new PlayerId(playerId), new TeamId(teamCId), new TeamId(teamDId), new Price(BigDecimal.valueOf(1000.0)), older));
        adapter.save(new Transfer(new TransferId(0L), new PlayerId(playerId), new TeamId(teamDId), new TeamId(teamCId), new Price(BigDecimal.valueOf(1200.0)), newer));

        PagedResult<Transfer> transfers = adapter.findByPlayerId(new PlayerId(playerId), 0, 10, "transferDate", "desc");

        assertEquals(2, transfers.content().size());
        assertEquals(0, BigDecimal.valueOf(1200.0).compareTo(transfers.content().getFirst().transferPrice().value()));
    }

    @Test
    @DisplayName("doit filtrer les transferts entrants et sortants d'une équipe")
    void shouldFindByTeamIncomingAndOutgoing() {
        Long teamEId = saveTeam("E", "E");
        Long teamFId = saveTeam("F", "F");
        Long teamGId = saveTeam("G", "G");
        Long player1Id = savePlayer(teamEId);
        Long player2Id = savePlayer(teamGId);

        adapter.save(new Transfer(new TransferId(0L), new PlayerId(player1Id), new TeamId(teamEId), new TeamId(teamFId), new Price(BigDecimal.valueOf(2000.0)), new Date()));
        adapter.save(new Transfer(new TransferId(0L), new PlayerId(player2Id), new TeamId(teamGId), new TeamId(teamEId), new Price(BigDecimal.valueOf(3000.0)), new Date()));

        PagedResult<Transfer> allForTeam = adapter.findByTeamId(new TeamId(teamEId), 0, 10, "transferDate", "desc");
        PagedResult<Transfer> outgoing = adapter.findOutgoingTransfers(new TeamId(teamEId), 0, 10, "transferPrice", "asc");
        PagedResult<Transfer> incoming = adapter.findIncomingTransfers(new TeamId(teamEId), 0, 10, "playerId", "desc");

        assertEquals(2, allForTeam.content().size());
        assertEquals(1, outgoing.content().size());
        assertEquals(1, incoming.content().size());
        assertEquals("transferPrice", outgoing.sortBy());
        assertEquals("playerId", incoming.sortBy());
    }

    @Test
    @DisplayName("doit trier les transferts entrants par prix croissant")
    void shouldSortIncomingTransfersByPriceAscending() {
        Long targetTeamId = saveTeam("Target", "TGT");
        Long sourceAId = saveTeam("SourceA", "SCA");
        Long sourceBId = saveTeam("SourceB", "SCB");
        Long player1Id = savePlayer(sourceAId);
        Long player2Id = savePlayer(sourceBId);

        adapter.save(new Transfer(new TransferId(0L), new PlayerId(player1Id), new TeamId(sourceAId), new TeamId(targetTeamId), new Price(BigDecimal.valueOf(9000.0)), new Date()));
        adapter.save(new Transfer(new TransferId(0L), new PlayerId(player2Id), new TeamId(sourceBId), new TeamId(targetTeamId), new Price(BigDecimal.valueOf(2000.0)), new Date()));

        PagedResult<Transfer> incoming = adapter.findIncomingTransfers(new TeamId(targetTeamId), 0, 10, "transferPrice", "asc");

        assertEquals(2, incoming.content().size());
        assertEquals(0, BigDecimal.valueOf(2000.0).compareTo(incoming.content().get(0).transferPrice().value()));
        assertEquals(0, BigDecimal.valueOf(9000.0).compareTo(incoming.content().get(1).transferPrice().value()));
    }

    @Test
    @DisplayName("doit inclure les recrutements avec source null dans les transferts entrants")
    void shouldIncludeRecruitmentTransfersWithNullSourceTeam() {
        Long targetTeamId = saveTeam("Recruit", "REC");
        Long playerId = savePlayer(targetTeamId);

        adapter.save(new Transfer(new TransferId(0L), new PlayerId(playerId), null, new TeamId(targetTeamId), new Price(BigDecimal.valueOf(1500.0)), new Date()));

        PagedResult<Transfer> incoming = adapter.findIncomingTransfers(new TeamId(targetTeamId), 0, 10, "transferDate", "desc");

        assertEquals(1, incoming.content().size());
        assertNull(incoming.content().getFirst().sourceTeamId());
        assertEquals(targetTeamId, incoming.content().getFirst().targetTeamId().value());
    }

    private Long saveTeam(String name, String acronym) {
        TeamJpa team = new TeamJpa();
        team.setName(name);
        team.setAcronym(acronym);
        team.setBudget(BigDecimal.valueOf(50000.0));
        team.setCreatedAt(new Date());
        team.setUpdatedAt(new Date());
        return teamSpringRepository.save(team).getId();
    }

    private Long savePlayer(Long teamId) {
        TeamJpa team = teamSpringRepository.findById(teamId).orElseThrow();

        PlayerJpa player = new PlayerJpa();
        player.setFirstName("P");
        player.setLastName("L");
        player.setAcronym("PL");
        player.setPosition(PositionEnum.CM);
        player.setPerformanceNote(7.0f);
        player.setMarketPrice(BigDecimal.valueOf(2000.0));
        player.setTitulaire(false);
        player.setTeam(team);
        player.setVersion(0);
        player.setCreatedAt(new Date());
        player.setUpdatedAt(new Date());
        return playerSpringRepository.save(player).getId();
    }
}

