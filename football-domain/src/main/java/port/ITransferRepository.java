package port;

import entity.Transfer;
import entity.TransferId;
import entity.PlayerId;
import entity.TeamId;
import java.util.List;
import java.util.Optional;

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
     * @return Liste de tous les transferts du joueur
     */
    List<Transfer> findByPlayerId(PlayerId playerId);
    
    /**
     * Récupère tous les transferts impliquant une équipe (en/out)
     * 
     * @param teamId ID de l'équipe
     * @return Liste de tous les transferts impliquant l'équipe
     */
    List<Transfer> findByTeamId(TeamId teamId);
    
    /**
     * Récupère les transferts sortants d'une équipe
     * 
     * @param teamId ID de l'équipe source
     * @return Liste des transferts sortants
     */
    List<Transfer> findOutgoingTransfers(TeamId teamId);
    
    /**
     * Récupère les transferts entrants vers une équipe
     * 
     * @param teamId ID de l'équipe cible
     * @return Liste des transferts entrants
     */
    List<Transfer> findIncomingTransfers(TeamId teamId);
}

