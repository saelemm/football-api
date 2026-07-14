package port;

import entity.Player;
import entity.PlayerId;
import entity.TeamId;

import java.util.Optional;
import pagination.PagedResult;

/**
 * Port de persistance pour les joueurs
 * 
 * Cette interface définit le contrat pour accéder à la persistance des joueurs.
 * L'implémentation concrète est dans l'adapter persistence-jpa.
 */
public interface IPlayerRepository {
    
    /**
     * Récupère un joueur par son ID
     * 
     * @param id ID du joueur
     * @return Le joueur s'il existe
     */
    Optional<Player> findById(PlayerId id);
    
    /**
     * Sauvegarde un joueur (création ou mise à jour)
     * 
     * @param player Le joueur à sauvegarder
     * @return ID du joueur sauvegardé
     */
    Long save(Player player);

    /**
     * Supprime un joueur par son ID
     * 
     * @param id ID du joueur à supprimer
     */
    void delete(PlayerId id);
    
    /**
     * Récupère tous les joueurs d'une équipe
     * 
     * @param teamId ID de l'équipe
     * @return Liste des joueurs de l'équipe
     */
    PagedResult<Player> findByTeamId(TeamId teamId, int page, int size, String sortBy, String direction);

    /**
     * Récupère les joueurs d'une équipe filtrés par titularisation.
     *
     * @param teamId ID de l'équipe
     * @param titulaire true pour les titulaires, false pour les remplaçants
     * @return Page de joueurs filtrés
     */
    PagedResult<Player> findByTeamIdAndTitulaire(TeamId teamId, boolean titulaire, int page, int size, String sortBy, String direction);
}

