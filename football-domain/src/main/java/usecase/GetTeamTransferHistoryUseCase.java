package usecase;

import Errors.TeamNotFoundException;
import entity.TeamId;
import entity.Transfer;
import pagination.PagedResult;

/**
 * Use Case (Query) pour récupérer l'historique des transferts d'une équipe
 *
 * - Orchestre la récupération des données via le repository
 * - Ne modifie rien (requête pure)
 * - La query/filtrage se fait via le repository, pas via les entités du domaine
 */
public interface GetTeamTransferHistoryUseCase {

    /**
     * Récupère tous les transferts impliquant une équipe
     *
     * @param teamId ID de l'équipe
     * @return Page de tous les transferts de l'équipe (in + out)
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    PagedResult<Transfer> findAllTransfers(TeamId teamId, int page, int size, String sortBy, String direction);

    /**
     * Récupère uniquement les transferts sortants d'une équipe
     *
     * @param teamId ID de l'équipe
     * @return Page des joueurs partis de cette équipe
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    PagedResult<Transfer> findOutgoing(TeamId teamId, int page, int size, String sortBy, String direction);

    /**
     * Récupère uniquement les transferts entrants d'une équipe
     *
     * @param teamId ID de l'équipe
     * @return Page des joueurs arrivés à cette équipe
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    PagedResult<Transfer> findIncoming(TeamId teamId, int page, int size, String sortBy, String direction);

}

