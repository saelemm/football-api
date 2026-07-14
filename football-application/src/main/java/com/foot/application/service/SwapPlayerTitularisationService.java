package com.foot.application.service;

import Errors.PlayerNotFoundException;
import Errors.TitularisationNotAllowedException;
import entity.Player;
import entity.PlayerId;
import entity.TeamId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import port.IPlayerRepository;
import usecase.SwapPlayerTitularisationUseCase;

import java.util.List;

import static Errors.ErrorMessages.AU_MOINS_UN_JOUEUR_DOIT_ETRE_RENSEIGNE;
import static Errors.ErrorMessages.LES_DEUX_JOUEURS_DOIVENT_ETRE_DIFFERENTS;
import static Errors.ErrorMessages.LE_JOUEUR_ENTRANT_DOIT_ETRE_NON_TITULAIRE;
import static Errors.ErrorMessages.LE_JOUEUR_SORTANT_DOIT_ETRE_TITULAIRE;

@Service
public class SwapPlayerTitularisationService implements SwapPlayerTitularisationUseCase {

    private final IPlayerRepository playerRepository;

    public SwapPlayerTitularisationService(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public void execute(TeamId teamId, PlayerId titulairePlayerId, PlayerId replacementPlayerId) {
        if (titulairePlayerId == null && replacementPlayerId == null) {
            throw new TitularisationNotAllowedException(AU_MOINS_UN_JOUEUR_DOIT_ETRE_RENSEIGNE);
        }

        if (titulairePlayerId != null && replacementPlayerId != null && titulairePlayerId.equals(replacementPlayerId)) {
            throw new TitularisationNotAllowedException(LES_DEUX_JOUEURS_DOIVENT_ETRE_DIFFERENTS);
        }

        List<Player> teamPlayers = playerRepository.findByTeamId(teamId, 0, 100, "name", "asc").content();

        if (titulairePlayerId != null && replacementPlayerId != null) {
            Player titulairePlayer = findPlayerInTeam(teamPlayers, teamId, titulairePlayerId);
            Player replacementPlayer = findPlayerInTeam(teamPlayers, teamId, replacementPlayerId);

            validateSwap(titulairePlayer, replacementPlayer);

            playerRepository.save(titulairePlayer.updateStats(titulairePlayer.stats().removeTitularisation()));
            playerRepository.save(replacementPlayer.updateStats(replacementPlayer.stats().assignTitularisation()));
            return;
        }

        if (titulairePlayerId != null) {
            Player titulairePlayer = findPlayerInTeam(teamPlayers, teamId, titulairePlayerId);
            if (!titulairePlayer.stats().isTitulaire()) {
                throw new TitularisationNotAllowedException(LE_JOUEUR_SORTANT_DOIT_ETRE_TITULAIRE);
            }
            playerRepository.save(titulairePlayer.updateStats(titulairePlayer.stats().removeTitularisation()));
            return;
        }

        Player replacementPlayer = findPlayerInTeam(teamPlayers, teamId, replacementPlayerId);
        if (replacementPlayer.stats().isTitulaire()) {
            throw new TitularisationNotAllowedException(LE_JOUEUR_ENTRANT_DOIT_ETRE_NON_TITULAIRE);
        }
        playerRepository.save(replacementPlayer.updateStats(replacementPlayer.stats().assignTitularisation()));
    }

    private void validateSwap(Player titulairePlayer, Player replacementPlayer) {
        if (!titulairePlayer.stats().isTitulaire()) {
            throw new TitularisationNotAllowedException(LE_JOUEUR_SORTANT_DOIT_ETRE_TITULAIRE);
        }

        if (replacementPlayer.stats().isTitulaire()) {
            throw new TitularisationNotAllowedException(LE_JOUEUR_ENTRANT_DOIT_ETRE_NON_TITULAIRE);
        }
    }

    private Player findPlayerInTeam(List<Player> teamPlayers, TeamId teamId, PlayerId playerId) {
        return teamPlayers.stream()
            .filter(player -> player.identifier().id().equals(playerId))
            .findFirst()
            .orElseThrow(() -> new PlayerNotFoundException(
                "Joueur introuvable dans l'équipe " + teamId.value() + " : " + playerId.value()));
    }
}

