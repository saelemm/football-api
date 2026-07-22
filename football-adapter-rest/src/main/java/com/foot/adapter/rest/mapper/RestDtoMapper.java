package com.foot.adapter.rest.mapper;

import com.foot.adapter.rest.dto.PlayerResponse;
import com.foot.adapter.rest.dto.TeamResponse;
import com.foot.adapter.rest.dto.TransferResponse;
import entity.Player;
import entity.Team;
import entity.Transfer;

import java.util.Comparator;
import java.util.List;

public final class RestDtoMapper {

    private RestDtoMapper() {
    }

    public static TeamResponse toResponse(Team team, List<Player> players) {
        List<PlayerResponse> playerResponses = players.stream()
            .sorted(
                Comparator.comparing((Player player) -> player.identifier().lastName(), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(player -> player.identifier().firstName(), String.CASE_INSENSITIVE_ORDER)
            )
            .map(RestDtoMapper::toResponse)
            .toList();
        List<TransferResponse> transferHistory = team.transferHistory().stream().map(RestDtoMapper::toResponse).toList();

        return new TeamResponse(
            team.teamId().teamId().value(),
            team.teamId().name(),
            team.teamId().acronym(),
            team.teamStat().budget(),
            team.teamStat().creation(),
            team.teamStat().lastUpdate(),
            playerResponses,
            transferHistory
        );
    }

    public static TeamResponse toResponse(Team team) {
        return toResponse(team, List.of());
    }

    public static PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
            player.identifier().id().value(),
            player.identifier().firstName(),
            player.identifier().lastName(),
            player.identifier().acronym(),
            player.identifier().teamId() == null ? null : player.identifier().teamId().value(),
            player.stats().position().name(),
            player.stats().performanceNote().value(),
            player.stats().marketPrice().value(),
            player.stats().isTitulaire(),
            player.version().version(),
            player.version().createdAt(),
            player.version().updatedAt()
        );
    }

    public static TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
            transfer.transferId().value(),
            transfer.playerId().value(),
            transfer.sourceTeamId() == null ? null : transfer.sourceTeamId().value(),
            transfer.targetTeamId().value(),
            transfer.transferPrice().value(),
            transfer.transferDate()
        );
    }
}

