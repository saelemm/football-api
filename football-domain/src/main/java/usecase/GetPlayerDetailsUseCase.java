package usecase;

import Errors.PlayerNotFoundException;
import entity.Player;
import entity.PlayerId;

/**
 * Use Case (Query) pour récupérer les détails d'un joueur
 *
 * Respecte l'architecture hexagonale:
 * - Orchestre la récupération des données
 * - Ne modifie rien (requête pure)
 * - Utilise le repository pour accéder aux données
 */
public interface GetPlayerDetailsUseCase {

    /**
     * Récupère les détails complets d'un joueur
     *
     * @param playerId ID du joueur
     * @return Le joueur avec tous ses détails (statistiques, équipe, version)
     *
     * @throws PlayerNotFoundException si le joueur n'existe pas
     */
    Player execute(PlayerId playerId);
}

