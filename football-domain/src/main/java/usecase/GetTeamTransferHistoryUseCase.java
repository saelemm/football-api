package usecase;

import Errors.TeamNotFoundException;
import entity.Transfer;
import entity.TeamId;
import java.util.List;

/**
 * Use Case (Query) pour récupérer l'historique des transferts d'une équipe
 *
 * Respecte l'architecture hexagonale:
 * - Orchestre la récupération des données via le repository
 * - Ne modifie rien (requête pure)
 * - La query/filtrage se fait via le repository, pas via les entités du domaine
 */
public interface GetTeamTransferHistoryUseCase {

    /**
     * Récupère tous les transferts impliquant une équipe
     *
     * @param teamId ID de l'équipe
     * @return Liste de tous les transferts de l'équipe (in + out)
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    List<Transfer> findAllTransfers(TeamId teamId);

    /**
     * Récupère uniquement les transferts sortants d'une équipe
     *
     * @param teamId ID de l'équipe
     * @return Liste des joueurs partis de cette équipe
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    List<Transfer> findOutgoing(TeamId teamId);

    /**
     * Récupère uniquement les transferts entrants d'une équipe
     *
     * @param teamId ID de l'équipe
     * @return Liste des joueurs arrivés à cette équipe
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     */
    List<Transfer> findIncoming(TeamId teamId);

}

