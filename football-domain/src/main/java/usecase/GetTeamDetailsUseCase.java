package usecase;

import Errors.TeamNotFoundException;
import entity.Player;
import entity.Team;
import entity.TeamId;
import org.aspectj.weaver.ast.Test;

import java.util.List;

/**
 * Use Case (Query) pour récupérer les détails d'une équipe
 *
 * Respecte l'architecture hexagonale:
 * - Orchestre la récupération des données
 * - Ne modifie rien (requête pure)
 * - Utilise le repository pour accéder aux données
 */
public interface GetTeamDetailsUseCase {

    /**
     * Récupère la liste de toutes les équipes
     * @return  Liste de toutes les équipes
     */
    List<Team> findAllTeams();

    /**
     * Récupère les détails complets d'une équipe
     *
     * @param teamId ID de l'équipe
     * @return L'équipe avec tous ses détails (joueurs, budget, historique)
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    Team execute(TeamId teamId);

    /**
     * Récupère la liste actuelle des joueurs d'une équipe.
     *
     * @param teamId ID de l'équipe
     * @return Liste actuelle des joueurs de l'équipe
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    List<Player> findCurrentPlayers(TeamId teamId);

    /**
     * Récupère la liste des titulaires d'une équipe.
     *
     * @param teamId ID de l'équipe
     * @return Liste des joueurs titulaires
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    List<Player> findTitulaires(TeamId teamId);

    /**
     * Récupère la liste des remplaçants d'une équipe.
     *
     * @param teamId ID de l'équipe
     * @return Liste des joueurs remplaçants
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    List<Player> findRemplacants(TeamId teamId);
}

