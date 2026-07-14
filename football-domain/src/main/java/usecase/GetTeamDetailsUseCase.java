package usecase;

import Errors.TeamNotFoundException;
import entity.Player;
import entity.Team;
import entity.TeamId;
import pagination.PagedResult;

/**
 * Use Case pour récupérer les détails d'une équipe
 *
 * - Orchestre la récupération des données
 * - Utilise le repository pour accéder aux données
 */
public interface GetTeamDetailsUseCase {

    /**
     * Récupère la liste de toutes les équipes
     * @return  Liste de toutes les équipes
     */
    PagedResult<Team> findAllTeams(int page, int size, String sortBy, String direction);

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
    PagedResult<Player> findCurrentPlayers(TeamId teamId, int page, int size, String sortBy, String direction);

    /**
     * Récupère la liste des titulaires d'une équipe.
     *
     * @param teamId ID de l'équipe
     * @return Page des joueurs titulaires
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    PagedResult<Player> findTitulaires(TeamId teamId, int page, int size, String sortBy, String direction);

    /**
     * Récupère la liste des remplaçants d'une équipe.
     *
     * @param teamId ID de l'équipe
     * @return Page des joueurs remplaçants
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    PagedResult<Player> findRemplacants(TeamId teamId, int page, int size, String sortBy, String direction);
}

