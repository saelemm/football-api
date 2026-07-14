package port;

import entity.Transfer;
import entity.TransferId;
import entity.PlayerId;
import entity.TeamId;
import java.util.Optional;
import pagination.PagedResult;

/**
 * Port de persistance pour les transferts
 * 
 * Cette interface définit le contrat pour accéder à la persistance des transferts.
 * L'implémentation concrète est dans l'adapter persistence-jpa.
 */
public interface ITransferRepository {
    
    /**
     * Récupère un transfert par son ID
     * 
     * @param id ID du transfert
     * @return Le transfert s'il existe
     */
    Optional<Transfer> findById(TransferId id);
    
    /**
     * Sauvegarde un transfert (création ou mise à jour)
     * 
     * @param transfer Le transfert à sauvegarder
     * @return ID du transfert sauvegardé
     */
    Long save(Transfer transfer);

    /**
     * Récupère tous les transferts d'un joueur
     * 
     * @param playerId ID du joueur
     * @return Page de tous les transferts du joueur
     */
    PagedResult<Transfer> findByPlayerId(PlayerId playerId, int page, int size, String sortBy, String direction);

    /**
     * Récupère tous les transferts impliquant une équipe (en/out)
     * 
     * @param teamId ID de l'équipe
     * @return Page de tous les transferts impliquant l'équipe
     */
    PagedResult<Transfer> findByTeamId(TeamId teamId, int page, int size, String sortBy, String direction);

    /**
     * Récupère les transferts sortants d'une équipe
     * 
     * @param teamId ID de l'équipe source
     * @return Page des transferts sortants
     */
    PagedResult<Transfer> findOutgoingTransfers(TeamId teamId, int page, int size, String sortBy, String direction);

    /**
     * Récupère les transferts entrants vers une équipe
     * 
     * @param teamId ID de l'équipe cible
     * @return Page des transferts entrants
     */
    PagedResult<Transfer> findIncomingTransfers(TeamId teamId, int page, int size, String sortBy, String direction);
}

